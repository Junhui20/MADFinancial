package com.uccd3223.madfinancial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "transactions.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_COLOR = "category_color";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IS_RECURRING = "is_recurring";
    private static final String COLUMN_RECURRING_PERIOD = "recurring_period";
    private static final String COLUMN_IMAGE_URI = "image_uri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_COLOR + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IS_RECURRING + " INTEGER,"
                + COLUMN_RECURRING_PERIOD + " TEXT,"
                + COLUMN_IMAGE_URI + " TEXT"
                + ")";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public Transaction getTransactionById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            COLUMN_ID + " = ?",
            new String[]{id},
            null,
            null,
            null
        );

        Transaction transaction = null;
        if (cursor != null && cursor.moveToFirst()) {
            transaction = new Transaction(
                cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_COLOR)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_IS_RECURRING)) == 1,
                cursor.getString(cursor.getColumnIndex(COLUMN_RECURRING_PERIOD)),
                cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI))
            );
            cursor.close();
        }
        return transaction;
    }

    public void updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_NAME, transaction.getName());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_CATEGORY_NAME, transaction.getCategoryName());
        values.put(COLUMN_CATEGORY_COLOR, transaction.getCategoryColor());
        values.put(COLUMN_TIMESTAMP, transaction.getTimestamp());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_IS_RECURRING, transaction.isRecurring() ? 1 : 0);
        values.put(COLUMN_RECURRING_PERIOD, transaction.getRecurringPeriod());
        values.put(COLUMN_IMAGE_URI, transaction.getImageUri());

        db.update(
            TABLE_TRANSACTIONS,
            values,
            COLUMN_ID + " = ?",
            new String[]{transaction.getId()}
        );
    }

    public long insertTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", transaction.getId());
        values.put("type", transaction.getType());
        values.put("amount", transaction.getAmount());
        values.put("name", transaction.getName());
        values.put("category", transaction.getCategory());
        values.put("category_name", transaction.getCategoryName());
        values.put("category_color", transaction.getCategoryColor());
        values.put("timestamp", transaction.getTimestamp());
        values.put("description", transaction.getDescription());
        values.put("is_recurring", transaction.isRecurring() ? 1 : 0);
        values.put("recurring_period", transaction.getRecurringPeriod());
        values.put("image_uri", transaction.getImageUri());

        long newRowId = db.insert("transactions", null, values);
        db.close();
        return newRowId;
    }
}
