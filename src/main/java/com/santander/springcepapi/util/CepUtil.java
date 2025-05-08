package com.santander.springcepapi.util;

public class CepUtil {

    public static String getNormalizado(String cep) {
        if (cep == null) {
            return null;
        }
        return cep.replaceAll(RegexEnum.APENAS_NUMEROS.getValue(), "");
    }

    public static String getFormatado(String cep) {
        if (cep == null) {
            return null;
        }

        cep = getNormalizado(cep);

        if (cep.length() == 8) {
            return String.format("%s-%s",
                    cep.substring(0, 5),
                    cep.substring(5));
        }

        return cep;
    }

}
