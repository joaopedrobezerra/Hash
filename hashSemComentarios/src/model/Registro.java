
package model;

public class Registro {
    private final int codigo;

    public Registro(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getCodigoPadded() {
        String s = Integer.toString(codigo);

        if (s.length() >= 9) return s;

        String result = "";
        for (int i = 0; i < 9 - s.length(); i++) result += '0';
        result += s;
        return result;
    }
}
