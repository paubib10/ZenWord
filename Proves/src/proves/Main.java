package proves;

import java.util.HashMap;
import java.util.Map;

public class Main {
    
    public static void main(String[] args) {
        System.out.println(esPalabraSolucio("puresa", "apres"));   // true
        System.out.println(esPalabraSolucio("puresa", "apresa"));   // false
        System.out.println(esPalabraSolucio("copta", "coa"));         // true
        System.out.println(esPalabraSolucio("saca", "casa"));           // true
        System.out.println(esPalabraSolucio("feiner", "ene"));      // true
        System.out.println(esPalabraSolucio("nado", "dona"));         // true
        System.out.println(esPalabraSolucio("nimfa", "fama"));         // false
    }

    public static boolean esPalabraSolucio(String palabra1, String palabra2) {
        Map<Character, Integer> letrasDisponibles = construirCatalogo(palabra1);

        // Verificar si podemos construir la palabra2 utilizando las letras disponibles
        for (char letra : palabra2.toCharArray()) {
            if (!letrasDisponibles.containsKey(letra) || letrasDisponibles.get(letra) == 0) {
                return false; // La letra no está disponible en la palabra1 o se ha agotado
            }
            letrasDisponibles.put(letra, letrasDisponibles.get(letra) - 1);
        }
        return true;
    }

    private static Map<Character, Integer> construirCatalogo(String palabra) {
        Map<Character, Integer> catalogo = new HashMap<>();
        for (char letra : palabra.toCharArray()) {
            catalogo.put(letra, catalogo.getOrDefault(letra, 0) + 1);
        }
        return catalogo;
    }
}


