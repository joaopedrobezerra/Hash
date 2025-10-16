
package model;

public class Registro {
    private final int codigo; // Código do registro (até 9 dígitos).

    public Registro(int codigo) {
        this.codigo = codigo; // Inicializa o código do registro.
    }

    public int getCodigo() {
        return codigo; // Retorna o código do registro.
    }

    public String getCodigoPadded() {
        String s = Integer.toString(codigo); // Converte o código para string.

        if (s.length() >= 9) return s; // Se já tem 9 ou mais dígitos, retorna como está.

        String result = ""; // String para acumular resultado.
        for (int i = 0; i < 9 - s.length(); i++) result += '0'; // Adiciona zeros à esquerda para completar 9 dígitos.
        result += s; // Adiciona o código original.
        return result; // Retorna o código formatado com 9 dígitos.
    }
}