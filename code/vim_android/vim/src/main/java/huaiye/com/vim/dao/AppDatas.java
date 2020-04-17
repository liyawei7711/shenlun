package huaiye.com.vim.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.AppMessages;
import ttyy.com.datasdao.Core;
import ttyy.com.datasdao.DaoBuilder;
import ttyy.com.datasdao.Datas;
import ttyy.com.sp.multiprocess.AppStores;
import ttyy.com.sp.multiprocess.StoreIntf;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppDatas
 */

public class AppDatas {

    static final int VERSION = 9;
    static final String DBNAME = "VIM";

    static AppConstants CONSTANTS;
    static AppDatabase db;

    private AppDatas() {

    }

    public static void init(Context context) {
        context = context.getApplicationContext();

        DaoBuilder builder = DaoBuilder.from(context)
                .setDebug(true)
                .setVersion(VERSION)
                .setDbName(DBNAME)
                .setDbDir(context.getExternalFilesDir("dbs").getAbsolutePath())
                .setCallback(new DaoBuilder.Callback() {
                    @Override
                    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                        Datas.from(sqLiteDatabase).dropAllTables();
                    }

                    @Override
                    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

                    }
                });

        Datas.createSqliteDatabase(builder);
        AppStores.get(context);

        db = Room.databaseBuilder(context, AppDatabase.class, "vim_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addMigrations(migration_1_new, migration_2_new, migration_3_new, migration_4_new, migration_7_8_new, migration_8_9_new)
                .build();

        CONSTANTS = new AppConstants();
    }


    public static Core DB() {
        return Datas.from(DBNAME);
    }

    public static AppDatabase MsgDB() {
        return db;
    }

    public static StoreIntf SP() {
        return AppStores.get(null);
    }

    public static AppConstants Constants() {
        return CONSTANTS;
    }

    public static AppAuth Auth() {
        return AppAuth.get();
    }

    public static AppMessages Messages() {
        return AppMessages.get();
    }


    static Migration migration_1_new = new Migration(1, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            dealAddColumn(database);
        }
    };
    static Migration migration_2_new = new Migration(2, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            dealAddColumn(database);
        }
    };
    static Migration migration_3_new = new Migration(3, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            dealAddColumn(database);
        }
    };
    static Migration migration_4_new = new Migration(4, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            dealAddColumn(database);
        }
    };
    static Migration migration_7_8_new = new Migration(7, VERSION) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                database.execSQL("ALTER TABLE tb_chat_single_msg "
                        + " ADD COLUMN extend1 INTEGER");
                database.execSQL("ALTER TABLE tb_chat_group_msg "
                        + " ADD COLUMN extend1 INTEGER");

                database.execSQL("ALTER TABLE tb_chat_single_msg "
                        + " ADD COLUMN extend2 STRING");
                database.execSQL("ALTER TABLE tb_chat_group_msg "
                        + " ADD COLUMN extend2 STRING");

                database.execSQL("ALTER TABLE tb_chat_single_msg "
                        + " ADD COLUMN extend3 STRING");
                database.execSQL("ALTER TABLE tb_chat_group_msg "
                        + " ADD COLUMN extend3 STRING");
            } catch (Exception e) {
            }
        }
    };
    static Migration migration_8_9_new = new Migration(7, VERSION) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                database.execSQL("ALTER TABLE tb_chat_single_msg "
                        + " ADD COLUMN summary INTEGER");
                database.execSQL("ALTER TABLE tb_chat_group_msg "
                        + " ADD COLUMN summary INTEGER");
            } catch (Exception e) {
            }
        }
    };

    private static void dealAddColumn(@NonNull SupportSQLiteDatabase database) {
        try {
            database.execSQL("ALTER TABLE tb_chat_single_msg "
                    + " ADD COLUMN bEncrypt INTEGER");
            database.execSQL("ALTER TABLE tb_chat_group_msg "
                    + " ADD COLUMN bEncrypt INTEGER");

            database.execSQL("ALTER TABLE tb_chat_single_msg "
                    + " ADD COLUMN fileName STRING");
            database.execSQL("ALTER TABLE tb_chat_group_msg "
                    + " ADD COLUMN fileName STRING");
        } catch (Exception e) {
        }
    }

}
