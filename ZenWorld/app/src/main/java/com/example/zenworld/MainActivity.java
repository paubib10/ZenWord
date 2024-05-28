package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zenworld.R.id;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    // Variables de instancia
    private TextView textViewPalabra;
    private TextView textViewInformacion;
    private TextView textViewBonus;
    private List<Character> letrasCirculo = new ArrayList<>();
    private int[] btnIdsLetra = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,
            R.id.button5,R.id.button6,R.id.button7};
    private ConstraintLayout constraintLayout;
    private int heightDisplay, widthDisplay;
    private TextView[] CtextViews;
    private List<TextView[]> textViewsList = new ArrayList<>();
    private int colorIndex = 0;
    Map<String, String> catalogoPalabras = new HashMap<>();
    Map<Integer, Set<String>> catalogoLongitudes = new HashMap<>();
    Map<Integer, Set<String>> catalogoSoluciones = new HashMap<>();
    Map<Integer, String> catalogoPalabrasOcultas = new TreeMap<>();
    Map<String, Integer> catalogoSolucionesEncontradas = new HashMap<>();
    Map<Character, Integer> catalogoLetrasDisponibles = new HashMap<>();
    private String palabraIntroducida;
    private int bonus = 0;
    private int paraulesEncertades = 0;
    private int paraulesPosiblesSolucions = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leerArchivo();
        inicializarVariables();
        configurarBotones();
        configurarPalabrasOcultas();
    }

    private void inicializarVariables() {
        textViewPalabra = findViewById(R.id.textView1);
        constraintLayout = findViewById(R.id.constraintLayout);
        textViewInformacion = findViewById(R.id.textView2);
        textViewBonus = findViewById(R.id.textView3);
        paraulesEncertades = 0;
        paraulesPosiblesSolucions = 1;
        bonus = 0;

        // ASIGNAR LETRAS A BOTONES
        String palabraAleatoria = obtenerPalabraAleatoria();
        System.out.println("Palabra aleatoria: " + palabraAleatoria);
        obtenerCatalogoSoluciones(palabraAleatoria);
        seleccionarPalabrasOcultas(palabraAleatoria);
        asignarLetrasABotones(palabraAleatoria.toUpperCase());

        // Actualizar el texto del textViewBonus
        textViewBonus.setText(String.valueOf(bonus));

        // ASIGNAR TEXTO INFORMATIVO
        textViewInformacion.setText("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): \n");

        if (catalogoSoluciones.isEmpty()) {
            System.out.println("El catálogo de soluciones está vacío.");
            return;
        } else {
            for (Iterator<Map.Entry<Integer, Set<String>>> iterator = catalogoSoluciones.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Set<String>> entry = iterator.next();
                int longitud = entry.getKey();
                Set<String> soluciones = entry.getValue();

                System.out.println("Longitud: " + longitud);
                System.out.println("Soluciones:");

                for (String solucion : soluciones) {
                    System.out.println(solucion);
                }

                System.out.println("------");
            }
        }

        if (catalogoPalabrasOcultas.isEmpty()) {
            System.out.println("El catálogo de palabras ocultas está vacío.");
            return;
        } else {
            System.out.println(catalogoPalabrasOcultas);
        }
        palabraIntroducida = " ";

        // PALABRAS OCULTAS CUADRADITOS
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightDisplay = displayMetrics.heightPixels;
        widthDisplay = displayMetrics.widthPixels;
    }

    private void configurarBotones() {
        configurarBotonClear();
        configurarRandom();
        configurarBotonBonus();
        configurarBotonAyuda();
        configurarBotonReiniciar();
        configurarBotonSend();
    }

    private void configurarBotonClear() {
        Button btnClear = findViewById(R.id.button9);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPalabra(view);
            }
        });
    }

    private void configurarRandom() {
        ImageButton btnRandom = findViewById(R.id.imageButton1);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomCircle();
            }
        });

        // Recorremos los botones de letras en el círculo
        for(int i = 0; i < btnIdsLetra.length; i++) {
            Button btn = findViewById(btnIdsLetra[i]); // get id
            char letra = btn.getText().charAt(0); // get letter on circle
            // Añadimos la letra en el circulo si no existe en la lista
            if (!letrasCirculo.contains(letra)) {
                letrasCirculo.add(letra);
            }
        }
    }

    private void configurarBotonAyuda() {
        ImageButton btnAyuda = findViewById(id.imageButton4);
        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bonus >= 2) {
                    // Seleccionar una palabra oculta no completada de forma aleatoria
                    List<Integer> indicesNoCompletados = new ArrayList<>();
                    for (Map.Entry<Integer, String> entry : catalogoPalabrasOcultas.entrySet()) {
                        if (!catalogoSolucionesEncontradas.containsKey(entry.getValue())) {
                            indicesNoCompletados.add(entry.getKey());
                        }
                    }
                    if (!indicesNoCompletados.isEmpty()) {
                        int randomIndex = new Random().nextInt(indicesNoCompletados.size());
                        int indiceAleatorioNoCompletado = indicesNoCompletados.get(randomIndex);
                        String palabraAleatoria = catalogoPalabrasOcultas.get(indiceAleatorioNoCompletado);

                        // Mostrar la primera letra de la palabra oculta
                        mostraPrimeraLletra(palabraAleatoria.toUpperCase(), indiceAleatorioNoCompletado);

                        // Restar 5 bonus
                        bonus -= 2;

                        // Actualizar el texto del textViewBonus
                        textViewBonus.setText(String.valueOf(bonus));
                    } else {
                        // No hay palabras ocultas para mostrar la primera letra
                    }
                }
            }
        });
    }

    private void configurarBotonReiniciar() {
        ImageButton btnReiniciar = findViewById(id.imageButton3);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarJuego();
            }
        });
    }

    private void configurarBotonSend() {
        Button sendButton = findViewById(R.id.button10);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palabraIntroducida = textViewPalabra.getText().toString();

                boolean esSolucion = false;
                for (Map.Entry<Integer, String> entry : catalogoPalabrasOcultas.entrySet()) {
                    Integer key = entry.getKey();
                    String value = entry.getValue();

                    if (esPalabraSolucio(value, palabraIntroducida.toLowerCase())) {
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.BLACK);
                        mostraMissatge("Has descobert una paraula amagada", false);
                        muestraPalabra(palabraIntroducida, key);
                        paraulesEncertades++;

                        // Eliminar la palabra del catalogo de palabras ocultas
                        catalogoPalabrasOcultas.remove(key);
                        esSolucion = true;
                        break;
                    }
                }

                // Si la palabra introducida no es una de las palabras ocultas pero es una solución posible
                if(!esSolucion && catalogoPalabras.containsValue(convertirSinAcentos(palabraIntroducida).toLowerCase())){
                    // Verificar si la palabra ya ha sido introducida
                    if (!catalogoSolucionesEncontradas.containsKey(palabraIntroducida)) {
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.BLACK);
                        mostraMissatge("Paraula vàlida! Tens un bonus", false);
                        paraulesEncertades++;

                        bonus++;

                        textViewBonus.setText(String.valueOf(bonus));
                    } else { // La palabra ya ha sido introducida
                        mostraMissatge("Aquesta ja la tens", false);
                        // Cambiamos el color de la palabra repetida a rojo
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.RED);
                    }
                } else if (!esSolucion) {
                    mostraMissatge("Paraula no vàlida!", false);
                }

                // ASIGNAR MENU INFORMATIVO
                textViewInformacion.setText("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): ");
                StringBuilder message = new StringBuilder();
                for(Map.Entry<String, Integer> entry : catalogoSolucionesEncontradas.entrySet()) {
                    String palabra = entry.getKey();
                    int color = entry.getValue();

                    message.append(palabra + ", ");
                }

                // Ya se han encontrado todas las palabras, catalagoPalabrasOcultas está vacío
                if (catalogoPalabrasOcultas.isEmpty()) {
                    mostraMissatge("Has guanyat! Enhorabona", false);
                    message.append(".");
                    disableViews(constraintLayout.getId());
                }

                textViewInformacion.append(Html.fromHtml(message.toString()));
                textViewPalabra.setText("");
                for (int btnId : btnIdsLetra) {
                    Button button = findViewById(btnId);
                    button.setEnabled(true);
                    button.setTextColor(Color.WHITE);
                }
            }
        });
    }

    private static String convertirSinAcentos(String input) {
        // Convertir la cadena a minúsculas y eliminar los acentos
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        return normalized.toLowerCase();
    }

    private void configurarBotonBonus() {
        ImageButton btnBonus = findViewById(id.imageButton2);
        btnBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarVentanaEmergeneteBonus();
            }
        });
    }

    private void configurarPalabrasOcultas() {
        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};

        for (int i = 0; i < guias.length; i++) {
            String palabraOculta = catalogoPalabrasOcultas.get(i);
            if (palabraOculta != null) {
                int numLetras = palabraOculta.length();
                CtextViews = crearFilaTextViewsGood(guias[i], numLetras);
                for (TextView textView : CtextViews) {

                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradientDrawable.setCornerRadius(10);
                    gradientDrawable.setStroke(2, Color.TRANSPARENT);
                    gradientDrawable.setColor(Color.CYAN);

                    // Establecer el GradientDrawable como fondo del TextView
                    textView.setBackground(gradientDrawable);

                    //textView.setBackgroundColor(Color.CYAN );
                }
                textViewsList.add(CtextViews); // Agregar el array de TextViews a la lista
            }
        }
    }

    private void asignarLetrasABotones(String palabra) {
        // Dividir la palabra en letras
        char[] letras = palabra.toCharArray();

        // Recorrer los botones de letras en el círculo
        for (int i = 0; i < btnIdsLetra.length; i++) {
            Button button = findViewById(btnIdsLetra[i]);
            // Verificar si hay letras disponibles
            if (i < letras.length) {
                // Asignar la letra al botón
                button.setText(String.valueOf(letras[i]));
                // Habilitar el botón y hacerlo visible
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);

                // Establecer OnClickListener para el botón
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtener la letra del botón
                        Button clickedButton = (Button) v;
                        String letra = clickedButton.getText().toString();

                        // Actualizar el TextView con la letra
                        textViewPalabra.append(letra);

                        // Cambiar el color del botón y deshabilitarlo
                        clickedButton.setTextColor(Color.GRAY);
                        clickedButton.setEnabled(false);
                    }
                });
            } else {
                // Si no hay más letras, desactivar el botón y hacerlo invisible
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void clearPalabra(View view) {
        textViewPalabra.setText("");
        for (int btnId : btnIdsLetra) {
            Button button = findViewById(btnId);
            button.setEnabled(true);
            button.setTextColor(Color.WHITE);
        }
    }

    private void randomCircle() {
        if(letrasCirculo.size() == btnIdsLetra.length) {
            Collections.shuffle(letrasCirculo);
            for (int i = 0; i < btnIdsLetra.length; i++) {
                Button button = findViewById(btnIdsLetra[i]);
                button.setText(String.valueOf(letrasCirculo.get(i)));
            }
        } else {
            Log.e("Error", "No hay suficientes letras en el círculo");
        }
    }

    public void mostrarVentanaEmergeneteBonus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // ASIGNAR TÍTULO
        builder.setTitle("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): \n");

        StringBuilder message = new StringBuilder();
        for (Iterator<Map.Entry<String, Integer>> it = catalogoSolucionesEncontradas.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            String palabra = entry.getKey();
            int color = entry.getValue();

            // Si el color es rojo, añadir la palabra en rojo al mensaje
            if(color == Color.RED) {
                message.append("<font color='red'>").append(palabra.toLowerCase()).append("</font><br>");
            } else {
                message.append(palabra.toLowerCase()).append("<br>");
            }
        }

        builder.setMessage(Html.fromHtml(message.toString()));
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public TextView[] crearFilaTextViewsGood(int guia, int lletres) {
        TextView[] textViews = new TextView[lletres];

        // Establecer un tamaño fijo para cada TextView
        int width = 125; // Reemplaza FIXED_SIZE con el tamaño que quieres para tus TextViews

        // Crear un LinearLayout para contener los TextViews
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setId(View.generateViewId());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        constraintLayout.addView(linearLayout);

        // Configurar restricciones para el LinearLayout
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, guia, ConstraintSet.TOP, 0);
        constraintSet.applyTo(constraintLayout);

        for (int i = 0; i < lletres; i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setText("");
            textView.setTextSize(30);
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTypeface(null, Typeface.BOLD);

            // Añadir TextView al LinearLayout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            params.setMargins(5, 10, 5, 10);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
            textViews[i] = textView;
        }

        return textViews;
    }

    public static boolean esPalabraSolucio(String palabra1, String palabra2) {
        Map<Character, Integer> letrasDisponibles = construirCatalogo(palabra1);

        // Recorrer las letras de la palabra2
        for (char letra : palabra2.toCharArray()) {
            // Verificar si la letra está disponible en la palabra1
            if (!letrasDisponibles.containsKey(letra) || letrasDisponibles.get(letra) == 0) {
                return false; // La letra no está disponible en la palabra1 o se ha agotado
            }
            letrasDisponibles.put(letra, letrasDisponibles.get(letra) - 1);
        }
        // Verificamos que las palabras sean iguales (letras en la misma posición)
        return palabra1.equals(palabra2);
    }

    private static Map<Character, Integer> construirCatalogo(String palabra) {
        Map<Character, Integer> catalogo = new HashMap<>();
        for (char letra : palabra.toCharArray()) {
            catalogo.put(letra, catalogo.getOrDefault(letra, 0) + 1);
        }
        return catalogo;
    }

    private void muestraPalabra(String s, int posicion) {
        // Obtener el array de TextViews correspondiente a la posición dada
        TextView[] textViews = obtenerTextViewsPorPosicion(posicion);

        // Verificar si la longitud de la palabra es menor o igual al número de TextViews disponibles
        if (s.length() <= textViews.length) {
            // Mostrar la palabra en los TextViews correspondientes
            for (int i = 0; i < s.length(); i++) {
                textViews[i].setTextColor(Color.WHITE);
                textViews[i].setText(String.valueOf(s.charAt(i)));
            }
        } else {
            // Si la longitud de la palabra es mayor que el número de TextViews disponibles, mostrar un mensaje de error
            Log.e("Error", "La longitud de la palabra es mayor que el número de TextViews disponibles");
        }
    }

    // Método auxiliar para obtener los TextViews correspondientes a una posición dada
    private TextView[] obtenerTextViewsPorPosicion(int posicion) {
        // Verificar si la posición es válida
        if (posicion >= 0 && posicion < textViewsList.size()) {
            return textViewsList.get(posicion);
        } else {
            // Si la posición no es válida, devolver un array vacío
            Log.e("Error", "Posición no válida");
            return new TextView[0];
        }
    }

    private void mostraPrimeraLletra(String s, int posicio){
        // Obtener el array de TextViews correspondiente a la posición dada
        TextView[] textViews = obtenerTextViewsPorPosicion(posicio);

        // Verificar si la longitud de la palabra es menor o igual al número de TextViews disponibles
        if (s.length() <= textViews.length) {
            // Verificar si la posición ya está ocupada
            if (textViews[0].getText().toString().isEmpty()) {
                // Si no está ocupada, mostrar la letra en el TextView correspondiente
                textViews[0].setTextColor(Color.WHITE);
                textViews[0].setText(String.valueOf(s.charAt(0)));
            } else {
                // Si está ocupada, buscar otra línea donde no esté ocupada
                for (int i = 1; i < textViews.length; i++) {
                    if (textViews[i].getText().toString().isEmpty()) {
                        textViews[i].setTextColor(Color.WHITE);
                        textViews[i].setText(String.valueOf(s.charAt(0)));
                        break;
                    }
                }
            }
        } else {
            // Si la longitud de la palabra es mayor que el número de TextViews disponibles, mostrar un mensaje de error
            Log.e("Error", "La longitud de la palabra es mayor que el número de TextViews disponibles");
        }
    }

    private void reiniciarJuego() {
        // Colores y recursos de círculo
        int[] colorCasilla = {Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED, Color.BLUE};
        int[] circleResources = {R.drawable.green_circle, R.drawable.purple_circle, R.drawable.yellow_circle,
                R.drawable.cyan_circle, R.drawable.red_circle, R.drawable.blue_circle};

        enableViews(constraintLayout.getId());

        // Eliminar todos los TextViews existentes
        for (Iterator<TextView[]> it = textViewsList.iterator(); it.hasNext(); ) {
            TextView[] textViews = it.next();
            for (TextView textView : textViews) {
                ((ViewGroup) textView.getParent()).removeView(textView); // Eliminar el TextView del layout
            }
        }
        textViewsList.clear(); // Limpiar la lista de TextViews

        colorIndex = (colorIndex + 1) % colorCasilla.length; // Actualizar el índice de color

        // Generar un nuevo catálogo de palabras para la partida
        catalogoPalabras.clear();
        catalogoLongitudes.clear();
        catalogoSoluciones.clear();
        catalogoPalabrasOcultas.clear();
        catalogoSolucionesEncontradas.clear();

        leerArchivo();
        inicializarVariables();

        // Crear nuevas filas de TextViews para cada palabra en el nuevo catálogo
        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};
        for (int i = 0; i < guias.length; i++) {
            String palabraOculta = catalogoPalabrasOcultas.get(i);
            if (palabraOculta != null) {
                int numLetras = palabraOculta.length();
                TextView[] textViews = crearFilaTextViewsGood(guias[i], numLetras);
                for (TextView textView : textViews) {

                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradientDrawable.setCornerRadius(10);
                    gradientDrawable.setStroke(2, Color.TRANSPARENT);
                    gradientDrawable.setColor(colorCasilla[colorIndex]);

                    // Establecer el GradientDrawable como fondo del TextView
                    textView.setBackground(gradientDrawable);

                    //textView.setBackgroundColor(colorCasilla[colorIndex]);
                    textView.setText(""); // Vaciar las casillas

                    // Cambiar el color de las casillas y el círculo
                    ImageView imageViewCircle = findViewById(R.id.imageView);
                    imageViewCircle.setImageResource(circleResources[colorIndex]);
                }
                textViewsList.add(textViews); // Agregar el array de TextViews a la lista
            }
        }
    }

    private void enableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(true);
            }
        }
    }

    private void disableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(false);
            }
        }
    }

    private void mostraMissatge ( String s , boolean llarg ){
        Context context = getApplicationContext () ;
        CharSequence text = s;
        int duration = Toast . LENGTH_LONG ;
        if(!llarg){
            duration = Toast. LENGTH_SHORT ;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void leerArchivo() {
        try {
            InputStream is = getResources().openRawResource(R.raw.paraules2);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = r.readLine()) != null) {
                agregarPalabra(line);
            }

        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
        }
    }

    public void agregarPalabra(String line) {
        String[] partes = line.split(";");
        if (partes.length == 2) {
            String palabraConAcentos = partes[0].trim();
            String palabraSinAcentos = partes[1].trim();

            // Agregar la palabra con acentos y su versión sin acentos al catálogo
            catalogoPalabras.put(palabraConAcentos, palabraSinAcentos);

            // Agregar la palabra al catálogo de palabras por longitud
            int longitud = palabraConAcentos.length();

            // Si la clave no existe creamos una nueva entrada
            if (!catalogoLongitudes.containsKey(longitud)) {
                catalogoLongitudes.put(longitud, new HashSet<>());
            }
            catalogoLongitudes.get(longitud).add(palabraConAcentos);
            catalogoLongitudes.get(longitud).add(palabraSinAcentos);
        }
    }

    private String obtenerPalabraAleatoria() {
        // Obtener la longitud máxima de las palabras
        Collections.max(catalogoLongitudes.keySet());

        // Generar un número aleatorio entre 3 y el máximo número de letras
        int longitudAleatoria = new Random().nextInt(3) + 4; //preguntar si debe ser entre 3 y 7 o como esta
        System.out.println("Longitud aleatorio de letraS "+ longitudAleatoria);

        // Obtener el conjunto de palabras de la longitud aleatoria
        Set<String> palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);

        // Si no hay palabras de esa longitud, regresar null
        while (palabrasLongitudAleatoria == null || palabrasLongitudAleatoria.isEmpty()) {
            System.out.println("No se ha encontrado palabras con esta longitud, seguimos buscando ...");
            longitudAleatoria = new Random().nextInt(3) + 4; //peguntar si debe ser entre 3 y 7 o como esta
            System.out.println("Longitud aleatorio de letra "+ longitudAleatoria);

            // Obtener el conjunto de palabras de la longitud aleatoria
            palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);
        }

        // Convertir el conjunto a una lista para poder seleccionar una palabra aleatoria
        List<String> listaPalabrasLongitudAleatoria = new ArrayList<>(palabrasLongitudAleatoria);
        // Elegir una palabra aleatoria de ese conjunto
        String palabraAleatoria = listaPalabrasLongitudAleatoria.get(new Random().nextInt(listaPalabrasLongitudAleatoria.size()));

        // Agregar la palabra aleatoria al catálogo de soluciones
        if (!catalogoSoluciones.containsKey(palabraAleatoria.length())) {
            catalogoSoluciones.put(palabraAleatoria.length(), new TreeSet<>());
        }
        catalogoSoluciones.get(palabraAleatoria.length()).add(palabraAleatoria);

        return palabraAleatoria;
    }

    private void obtenerCatalogoSoluciones(String palabraAleatoria) {
        // Crear un mapa de letras únicas de la palabra aleatoria y sus recuentos
        Map<Character, Integer> letrasPalabraAleatoria = new HashMap<>();
        for (char letra : palabraAleatoria.toCharArray()) {
            letrasPalabraAleatoria.put(letra, letrasPalabraAleatoria.getOrDefault(letra, 0) + 1);
        }
        int longitud = palabraAleatoria.length();
        for (int i = 1; i < 5; i++) {
            if(longitud - i < 3){
                break;
            }
            Set<String> palabrasLongitud = catalogoLongitudes.get(longitud - i);
            while ((palabrasLongitud == null || palabrasLongitud.isEmpty() || palabrasLongitud.size() < 5) && (longitud - i) > 3) {
                palabrasLongitud = catalogoLongitudes.get(longitud - i);
            }
            for (Iterator<String> it = palabrasLongitud.iterator(); it.hasNext(); ) {
                String palabra = it.next();
                // Crear una copia de las letras únicas de la palabra aleatoria y sus recuentos
                Map<Character, Integer> letrasRestantes = new HashMap<>(letrasPalabraAleatoria);
                boolean contieneTodasLasLetras = true;
                for (char letra : palabra.toCharArray()) {
                    if (!letrasRestantes.containsKey(letra) || letrasRestantes.get(letra) == 0) {
                        contieneTodasLasLetras = false;
                        break;
                    }
                    letrasRestantes.put(letra, letrasRestantes.get(letra) - 1);
                }
                if (contieneTodasLasLetras) {
                    if (!catalogoSoluciones.containsKey(palabra.length())) {
                        catalogoSoluciones.put(palabra.length(), new TreeSet<>());
                    }
                    catalogoSoluciones.get(palabra.length()).add(palabra);
                    paraulesPosiblesSolucions++;
                }
            }
        }
    }

    private void seleccionarPalabrasOcultas(String palabraAleataroria) {
        int longitud = palabraAleataroria.length();
        Set<String> todasLasSoluciones = new HashSet<>();

        // Agregar todas las soluciones posibles al conjunto todasLasSoluciones
        for (int i = 1; i < 5; i++) {
            if(longitud - i < 3){
                break;
            }
            Set<String> palabrasSoluciones = catalogoSoluciones.get(longitud - i);
            if (palabrasSoluciones != null && !palabrasSoluciones.isEmpty()) {
                todasLasSoluciones.addAll(palabrasSoluciones);
            }
        }

        // Convertir el conjunto todasLasSoluciones a una lista para poder seleccionar palabras de manera aleatoria
        List<String> listaTodasLasSoluciones = new ArrayList<>(todasLasSoluciones);

        // Seleccionar aleatoriamente hasta 4 soluciones de listaTodasLasSoluciones
        Random rand = new Random();
        int contadorSoluciones = 0;
        while (contadorSoluciones < 4 && !listaTodasLasSoluciones.isEmpty()) {
            int indiceAleatorio = rand.nextInt(listaTodasLasSoluciones.size());
            String solucionAleatoria = listaTodasLasSoluciones.get(indiceAleatorio);

            // Verificar si la solución ya está en el catálogo de palabras ocultas o es la palabra aleatoria
            if (!catalogoPalabrasOcultas.containsValue(solucionAleatoria) && !solucionAleatoria.equals(palabraAleataroria)) {
                listaTodasLasSoluciones.remove(indiceAleatorio); // Eliminar la solución seleccionada de listaTodasLasSoluciones
                catalogoPalabrasOcultas.put(contadorSoluciones, solucionAleatoria);
                contadorSoluciones++;
            } else {
                // Si la solución ya está en el catálogo de palabras ocultas o es la palabra aleatoria, eliminarla de listaTodasLasSoluciones y no agregarla al catálogo
                listaTodasLasSoluciones.remove(indiceAleatorio);
            }
        }

        // Añadir la palabra aleatoria en la última posición del catálogo de palabras ocultas
        catalogoPalabrasOcultas.put(contadorSoluciones, palabraAleataroria);

        sortCatalogoPalabrasOcultas();
    }

    private void sortCatalogoPalabrasOcultas() {
        // Crear una lista para almacenar los valores del mapa
        List<String> listaDeValores = new ArrayList<>(catalogoPalabrasOcultas.values());

        // Ordenar la lista
        Collections.sort(listaDeValores, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int comparacionPorLongitud = Integer.compare(s1.length(), s2.length());
                if (comparacionPorLongitud == 0) {
                    return s1.compareTo(s2);
                } else {
                    return comparacionPorLongitud;
                }
            }
        });

        // Asegurarse de que no excedemos el tamaño de la lista
        int max = Math.min(4, listaDeValores.size());
        for (int i = 0; i < max; i++) {
            catalogoPalabrasOcultas.put(i, listaDeValores.get(i));
        }
    }
}