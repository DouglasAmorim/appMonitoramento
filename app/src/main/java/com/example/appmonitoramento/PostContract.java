package com.example.appmonitoramento;
import android.provider.BaseColumns;

public final class PostContract {
    private PostContract() {
    }

    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "dadosUsuario";
        public static final String COLUMN_NAME_USUARIO = "usuario";
        public static final String COLUMN_NAME_TOKEN = "token";
    }
}
