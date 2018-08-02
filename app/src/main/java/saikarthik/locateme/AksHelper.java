package saikarthik.locateme;

/**
 * Created by anuraag on 18/10/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.version;


public class AksHelper extends SQLiteOpenHelper {

    public ArrayList getData(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList sname = new ArrayList();
        String[] cols = {"uname"};
        Cursor cur = db.query("user",cols,null,null,null,null,null);
        if(cur.moveToFirst())
            do {
                sname.add(cur.getString(0));
            }while(cur.moveToNext());
        db.close();

        return sname;

    }

    public void insertData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("uname",name);
        db.insert("user",null,cv);
        db.close();

    }
    public AksHelper(Context context) {
        super(context,"databasename.db",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q1 = "CREATE TABLE user(uname TEXT);";
        db.execSQL(q1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
