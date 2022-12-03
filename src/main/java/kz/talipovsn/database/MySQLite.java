package kz.talipovsn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MySQLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 46; // НОМЕР ВЕРСИИ БАЗЫ ДАННЫХ И ТАБЛИЦ !

    static final String DATABASE_NAME = "phones"; // Имя базы данных

    static final String TABLE_NAME = "cell_phones"; // Имя таблицы
    static final String ID = "id"; // Поле с ID
    static final String NAME = "name"; // Поле с наименованием телефона
    static final String NAME_LC = "name_lc"; // // Поле с наименованием телефона в нижнем регистре
    static final String MODEL = "model"; // Поле с наименованием модели
    static final String MODEL_LC = "model_lc"; // // Поле с наименованием модели в нижнем регистре
    static final String PRICE = "price"; // Поле с ценой
    static final String RESOLUTION = "resolution"; // Поле с разрешением дисплея
    static final String MEMORY = "memory"; // Поле с объемом памяти
    static final String RAM = "ram"; // Поле с объемом оперативной памяти

    static final String ASSETS_FILE_NAME = "phones.txt"; // Имя файла из ресурсов с данными для БД
    static final String DATA_SEPARATOR = "|"; // Разделитель данных в файле ресурсов с телефонами

    private Context context; // Контекст приложения



    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Метод создания базы данных и таблиц в ней
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + NAME_LC + " TEXT,"
                + MODEL + " TEXT,"
                + MODEL_LC + " TEXT,"
                + PRICE + " INTEGER,"
                + RESOLUTION + " TEXT,"
                + MEMORY + " INTEGER,"
                + RAM + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        System.out.println(CREATE_CONTACTS_TABLE);
        loadDataFromAsset(context, ASSETS_FILE_NAME,  db);


    }

    // Метод при обновлении структуры базы данных и/или таблиц в ней
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        System.out.println("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Добавление нового контакта в БД
    public void addData(SQLiteDatabase db, String name, String model, int price, String resolution, int memory, int ram) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(NAME_LC, name.toLowerCase());
        values.put(MODEL, model);
        values.put(MODEL_LC, model.toLowerCase());
        values.put(PRICE, price);
        values.put(RESOLUTION, resolution);
        values.put(MEMORY, memory);
        values.put(RAM, ram);
        db.insert(TABLE_NAME, null, values);
    }

    // Добавление записей в базу данных из файла ресурсов
    public void loadDataFromAsset(Context context, String fileName, SQLiteDatabase db) {
        BufferedReader in = null;

        try {
            // Открываем поток для работы с файлом с исходными данными
            InputStream is = context.getAssets().open(fileName);
            // Открываем буфер обмена для потока работы с файлом с исходными данными
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            while ((str = in.readLine()) != null) { // Читаем строку из файла
                String strTrim = str.trim(); // Убираем у строки пробелы с концов
                if (!strTrim.equals("")) { // Если строка не пустая, то
                    StringTokenizer st = new StringTokenizer(strTrim, DATA_SEPARATOR); // Нарезаем ее на части
                    String name = st.nextToken().trim();
                    String model = st.nextToken().trim();
                    String price = st.nextToken().trim();
                    String resolution = st.nextToken().trim();
                    String memory = st.nextToken().trim();
                    String ram = st.nextToken().trim();
                    addData(db, name, model, Integer.parseInt(price), resolution, Integer.parseInt(memory), Integer.parseInt(ram));
                }
            }

        // Обработчики ошибок
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

    }

    // Получение значений данных из БД в виде строки с фильтром
    public String getData(String filter, Spinner spinner) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME; // Переменная для SQL-запроса

        long idSpin = spinner.getSelectedItemId();

        if (idSpin == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + NAME_LC + " LIKE '%" + filter.toLowerCase() + "%'"
                    + " OR " + MODEL_LC + " LIKE '%" + filter.toLowerCase() + "%'"
                    + " OR " + RESOLUTION + " LIKE '%" + filter + "%'" + ")";
        } else if (idSpin == 1) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + NAME_LC + " LIKE '%" + filter.toLowerCase() + "%'" + ")";
        } else if (idSpin == 2) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + MODEL_LC + " LIKE '%" + filter.toLowerCase() + "%'" + ")";
        } else if (idSpin == 3) {
            if (filter.isEmpty() | !filter.matches("[-+]?\\d+") ) {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " LIMIT 0"  ;
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                        + PRICE + " >= " + Double.parseDouble(filter);
            }
        } else if (idSpin == 4) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE ("
                    + RESOLUTION + " LIKE '%" + filter + "%'" + ")";
        } else if (idSpin == 5) {
            if (filter.isEmpty() | !filter.matches("[-+]?\\d+") ) {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " LIMIT 0"  ;
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                        + MEMORY + " >= " + Integer.parseInt(filter);
            }
        } else if (idSpin == 6) {
            if (filter.isEmpty() | !filter.matches("[-+]?\\d+") ) {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " LIMIT 0"  ;
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                        + RAM + " >= " + Integer.parseInt(filter);
            }
        }

        SQLiteDatabase db = this.getReadableDatabase(); // Доступ к БД
        Cursor cursor = db.rawQuery(selectQuery, null); // Выполнение SQL-запроса

        StringBuilder data = new StringBuilder(); // Переменная для формирования данных из запроса


        int num = 0;
        if (cursor.moveToFirst()) { // Если есть хоть одна запись, то
            do { // Цикл по всем записям результата запроса
                int n = cursor.getColumnIndex(NAME);
                int m = cursor.getColumnIndex(MODEL);
                int p = cursor.getColumnIndex(PRICE);
                int res = cursor.getColumnIndex(RESOLUTION);
                int mem = cursor.getColumnIndex(MEMORY);
                int r = cursor.getColumnIndex(RAM);
                String name = cursor.getString(n);
                String model = cursor.getString(m);
                String price = cursor.getString(p);
                String resolution = cursor.getString(res);
                String memory = cursor.getString(mem);
                String ram = cursor.getString(r);
                data.append(String.valueOf(++num) + ") " + name + "\n "
                        + "Модель: " + model + "\n"
                        + "Цена (тг): " + price + "\n"
                        + "Разрешение дисплея: " + resolution + "\n"
                        + "Встроенная память (Гб): " + memory + "\n"
                        + "ОЗУ (Гб): " + ram + "\n");
            } while (cursor.moveToNext()); // Цикл пока есть следующая запись
        }
        return data.toString(); // Возвращение результата
    }

}