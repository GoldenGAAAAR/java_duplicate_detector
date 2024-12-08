import java.util.*;

public class NearDuplicateDetector {

    private static String canonicalizeText(String text) {
        text = text.replaceAll("\\s+", " ");
        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
        return text.trim();
    }

    private static List<String> generateShingles(String text, int shingleSize) {
        List<String> shingles = new ArrayList<>();
        for (int i = 0; i <= text.length() - shingleSize; i++) {
            shingles.add(text.substring(i, i + shingleSize));
        }
        return shingles;
    }

    private static List<Integer> createMinHashSignature(String text, int numHashes, int shingleSize, int hashRange) {
        List<String> shingles = generateShingles(canonicalizeText(text), shingleSize);
        Set<String> uniqueShingles = new HashSet<>(shingles);
        List<Integer> signature = new ArrayList<>(Collections.nCopies(numHashes, Integer.MAX_VALUE));

        //хэш-функция с регулируемым диапазоном
        for (String shingle : uniqueShingles) {
            for (int i = 0; i < numHashes; i++) {
                int hashValue = Math.abs((shingle.hashCode() + i * 31) % hashRange);
                if (hashValue < signature.get(i)) {
                    signature.set(i, hashValue);
                }
            }
        }
        return signature;
    }

    private static double jaccardSimilarity(List<Integer> signature1, List<Integer> signature2) {
        if (signature1.size() != signature2.size()) {
            throw new IllegalArgumentException("Signatures must have the same size");
        }
        int intersection = 0;
        for (int i = 0; i < signature1.size(); i++) {
            if (signature1.get(i).equals(signature2.get(i))) {
                intersection++;
            }
        }
        return (double) intersection / signature1.size();
    }

    public static void main(String[] args) {
        String text1 = "The most important kind of freedom is to be what you really are. " +
                "You trade in your reality for a role. You trade in your sense for an act. " +
                "You give up your ability to feel, and in exchange, put on a mask. " +
                "There can’t be any large-scale revolution until there’s a personal revolution, " +
                "on an individual level. It’s got to happen inside first.";
        String text2 = "The most important kind of freedom is to be what you really are. " +
                "You trade in your reality for a role. You trade in your sense for an act. " +
                "You give up your ability to feel, and in exchange, put on a mask. " +
                "There can’t be any large-scale revolution until there’s a personal revolution, ";
        String text3 = "This is a completely different text.";

        int numHashes = 500;       // Количество хэш-функций
        int shingleSize = 5;     // Размер shingles
        int hashRange = 1000;   // Диапазон хэш-значений

        try {
            List<Integer> signature1 = createMinHashSignature(text1, numHashes, shingleSize, hashRange);
            List<Integer> signature2 = createMinHashSignature(text2, numHashes, shingleSize, hashRange);
            List<Integer> signature3 = createMinHashSignature(text3, numHashes, shingleSize, hashRange);

            System.out.println("Similarity (text1, text2): " + jaccardSimilarity(signature1, signature2));
            System.out.println("Similarity (text1, text3): " + jaccardSimilarity(signature1, signature3));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}