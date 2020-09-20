package com.uhreckysw.balancer.backend.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.uhreckysw.balancer.Balancer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Database {
    private static final Database ourInstance = new Database();
    public static Database getInstance() {
        return ourInstance;
    }

    private final SQLiteDatabase db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Database() {
        // initialize
        db = SQLiteDatabase.openOrCreateDatabase(Balancer.getContext().getFilesDir().toString() + "/db.sqlite",null);
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("create table if not exists version(nr integer PRIMARY key);");
        db.execSQL("replace into version(nr) values(0);");

        // migrate
        switch (getVersion()) {
            case 0:
                db.execSQL("create table if not exists categories(name varchar(30) not null, primary key (name));");
                db.execSQL("create table if not exists payments(" +
                        "id integer PRIMARY key," +
                        "item varchar(30) not null," +
                        "price float," +
                        "description text," +
                        "date_of_buy date," +
                        "category varchar(30)," +
                        "FOREIGN key (category) REFERENCES categories(name));");
                db.execSQL("create table if not exists limits(" +
                        "id integer PRIMARY key," +
                        "name varchar(30) not null," +
                        "value float," +
                        "days integer," +
                        "category varchar(100))");
                db.execSQL("insert into version(nr) values(1);");
            case 1:
                db.execSQL("create table receipt(" +
                        "id varchar(50) PRIMARY key," +
                        "merchant varchar(50));");
                db.execSQL("create table receiptItems(" +
                        "receiptId varchar(50)," +
                        "name varchar(50) not null," +
                        "price float," +
                        "quantity integer," +
                        "FOREIGN key (receiptId) REFERENCES receipt(id));");
                db.execSQL("alter table payments add column receiptId varchar(50);");
                db.execSQL("insert into version(nr) values(2);");
            default:
                ;
        }
    }

    private int getVersion() {
        Cursor res = db.rawQuery( "select max(nr) from version", null );
        res.moveToFirst();
        int ret = res.getInt(0);
        res.close();
        return ret;
    }

    public void createCategory(String name) {
        db.execSQL("insert into categories(name) VALUES(?);", new Object[]{name});
    }

    public void ensureCategory(String name) {
        db.execSQL("insert or ignore into categories(name) VALUES(?);", new Object[]{name});
    }

    public ArrayList<String> getAllCategories() {
        ArrayList<String> ret = new ArrayList<>();
        Cursor res = db.rawQuery( "select name from categories", null );
        while(res.moveToNext()) ret.add(res.getString(0));
        res.close();
        return ret;
    }

    public void createPayment(String item, float price, Date date_of_buy, String category, String description, String receiptId) {
        db.execSQL("insert into payments(item, price, date_of_buy, category, description, receiptId) VALUES(?,?,?,?,?,?);",
                new Object[]{item, price, dateFormat.format(date_of_buy), category, description, receiptId});
    }

    public void editPayment(Payment payment) {
        editPayment(payment.id, payment.item, payment.price, payment.date_of_buy, payment.category, (payment.description == null ? "" : payment.description), (payment.receipt == null ? "" : payment.receipt.id));
    }

    private void editPayment(int id, String item, float price, Date date_of_buy, String category, String description, String receiptId) {
        db.execSQL("replace into payments(id, item, price, date_of_buy, category, description, receiptId) VALUES(?,?,?,?,?,?,?);",
                new Object[]{id, item, price, dateFormat.format(date_of_buy), category, description, receiptId});
        clearCategories();
    }

    public ArrayList<Payment> filterPayment(Filter filter, String predicate) {
        if ((filter.priceMin == 0) && filter.priceMax == 0) return new ArrayList<Payment>();
        return filterPayment(filter.priceMin, filter.priceMax, filter.dateMin, filter.dateMax, filter.category, predicate);
    }
    private ArrayList<Payment> filterPayment(float priceMin, float priceMax, Date dateMin, Date dateMax, String category, String predicate) {
        ArrayList<Payment> ret =  paymentsSelectQ( "" +
                " select id, item, price, date_of_buy, category, description, receiptId" +
                " from payments" +
                " where " +
                        " (date_of_buy >= ?) and" +
                        " (date_of_buy <= ?)" +
                        (((category == null) || category.isEmpty()) ? "" : " and category in (" + category + ")") +
                        (((predicate == null) || predicate.isEmpty()) ? "" :
                                " and ((lower(item) like '%' || lower('" + predicate
                                        + "') || '%') or (lower(description) like '%' || lower('" + predicate + "') || '%'))") +
                " order by date_of_buy desc", new String[]{dateFormat.format(dateMin), dateFormat.format(dateMax)});
        ret.removeIf(x->(x.price < priceMin) || (x.price > priceMax));
        return ret;
    }

    public void deletePayment(int id) {
        db.execSQL("DELETE FROM payments WHERE id = ?;", new Object[]{id});
        clearCategories();
        clearReceipts();
    }

    private ArrayList<Payment> paymentsSelectQ(String query, String[] args) {
        ArrayList<Payment> ret = new ArrayList<>();
        Cursor res = db.rawQuery( query, args );
        while(res.moveToNext()) {
            Payment payment = new Payment();
            try {
                payment = extractPayment(res);
            } catch (ParseException e) {
                payment.setItem(res.getString(0) + " [DATE_PARSE_FAILED_ERR]");
            }
            ret.add(payment);
        }
        res.close();
        return ret;
    }

    private Payment extractPayment(Cursor res) throws ParseException {
        return new Payment()
                .setId(res.getInt(0))
                .setItem(res.getString(1))
                .setPrice(res.getFloat(2))
                .setDate_of_buy(dateFormat.parse(res.getString(3)))
                .setCategory(res.getString(4))
                .setDescription(res.getString(5))
                .setReceipt(getReceipt(res.getString(6)));
    }

    public Pair<Float, Float> getPriceBoundaries(Date from, Date to, String category) {
        Cursor res;
        res = db.rawQuery("select min(price), max(price) from payments where (date_of_buy >= ?) and (date_of_buy <= ?)"
                        + ((category.isEmpty() || (category == null)) ? "" : " and category in (" + category + ")")
                , new String[]{dateFormat.format(from), dateFormat.format(to)});
        res.moveToFirst();
        Pair<Float, Float> ret = new Pair<>(res.getFloat(0), res.getFloat(1) + (float) 1.00);
        res.close();
        return ret;
    }

    public boolean isEmptyDb() {
        Cursor res = db.rawQuery( "select count(id) from payments", null );
        res.moveToFirst();
        boolean ret = (res.getInt(0) == 0);
        res.close();
        return ret;
    }

    private void clearCategories() {
        db.execSQL("DELETE FROM categories WHERE name not in" +
                "(SELECT category as name from payments GROUP BY category);", new Object[]{});
    }

    public ArrayList<Limit> getAllLimits() {
        ArrayList<Limit> ret = new ArrayList<>();
        Cursor res = db.rawQuery("select l.id, name, value, days, categories as category, sum(price) as spent" +
                " from" +
                " (WITH split(id, name, days, value, categories, cat, str) AS (" +
                "    SELECT id, name, days, value, category as categories, '', category||',' from limits" +
                "    UNION ALL SELECT" +
                "    id," +
                "    name," +
                "    days," +
                "    value," +
                "    categories," +
                "    substr(str, 0, instr(str, ','))," +
                "    substr(str, instr(str, ',')+1)" +
                "    FROM split WHERE str!=''" +
                " ) SELECT * FROM split WHERE cat!='') l" +
                " left join payments p on ((('''' || category || '''') = trim(cat)) and (date_of_buy > date('now', '-' || days || ' day')))" +
                " GROUP by l.id", null);
        while (res.moveToNext()) {
            ret.add(new Limit()
                    .setId(res.getInt(0))
                    .setName(res.getString(1))
                    .setValue(res.getFloat(2))
                    .setDays(res.getInt(3))
                    .setCategory(res.getString(4))
                    .setSpent(res.getFloat(5)));
        }
        res.close();
        return ret;
    }

    public void createLimit(Limit limit) {
        db.execSQL("insert into limits(name, value, days, category) VALUES(?,?,?,?);",
                new Object[]{limit.name, limit.value, limit.days, limit.category});
    }

    public void editLimit(Limit limit) {
        db.execSQL("replace into limits(id, name, value, days, category) VALUES(?,?,?,?,?);",
                new Object[]{limit.id, limit.name, limit.value, limit.days, limit.category});
    }

    public void deleteLimit(int id) {
        db.execSQL("DELETE FROM limits WHERE id = ?;", new Object[]{id});
    }

    public boolean createReceipt(String id, String merchant, Object[] items) {
        boolean success = true;
        db.beginTransaction();
        try {
            db.execSQL("insert into receipt(id, merchant) VALUES(?,?);", new Object[]{id, merchant});
            for (int i = 0; i < items.length; i+=3) {
                db.execSQL("insert into receiptItems(receiptId, name, price, quantity) VALUES(?,?,?,?);", new Object[]{id, items[i], items[i+1], items[i+2]});
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            success = (e instanceof SQLiteConstraintException); // receipt already is in database
        }
        finally {
            db.endTransaction();
        }
        return success;
    }

    private Receipt getReceipt(String receiptId) {
        if (receiptId.isEmpty()) return null;
        Cursor res = db.rawQuery( "SELECT merchant FROM receipt WHERE id=?", new String[]{receiptId} );
        if (!res.moveToFirst()) return null;
        Receipt ret = new Receipt().setId(receiptId).setMechant(res.getString(0));
        res.close();

        ArrayList<Receipt.Item> items = new ArrayList<>();
        res = db.rawQuery( "SELECT name, price, quantity FROM receiptItems WHERE receiptId=?", new String[]{receiptId} );
        while(res.moveToNext())
            items.add(new Receipt.Item().setName(res.getString(0)).setPrice(res.getFloat(1)).setQuantity(res.getInt(2)));
        res.close();

        return ret.setItems(items);
    }

    private void clearReceipts() {
        db.execSQL("DELETE FROM receiptItems WHERE receiptId not in" +
                "(SELECT DISTINCT receiptId from payments);", new Object[]{});
        db.execSQL("DELETE FROM receipt WHERE id not in" +
                "(SELECT DISTINCT receiptId from payments);", new Object[]{});
    }

}
