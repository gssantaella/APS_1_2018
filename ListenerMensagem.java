package com.aps;

import java.util.ArrayList;

public interface ListenerMensagem {
    public boolean recebeMsg(String tipo, String quemEnviou, ArrayList<String> contatos, String msgCorpo);
}
