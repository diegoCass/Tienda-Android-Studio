package com.example.shoppi.admin;

import java.util.regex.Pattern;

public class ProducerBuilder {

    // Corregir la expresión regular para capturar correctamente el texto entre ${ y }
    Pattern pattern = Pattern.compile("\\$\\{([^}]*)}");

}
