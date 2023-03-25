import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> database = new HashMap<>();
    private final Set<String> stopList = new HashSet<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File fileStop = new File("stop-ru.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(fileStop))) {
            while (reader.ready()) {
                stopList.add(reader.readLine());
            }
        } catch (IOException e) {
            e.getMessage();
        }
        for (File pdf : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int countPages = doc.getNumberOfPages();
            for (int i = 1; i <= countPages; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (String word : freqs.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, freqs.get(word));
                    if (database.containsKey(word)) {
                        database.get(word).add(pageEntry);
                    } else {
                        database.put(word, new ArrayList<>());
                        database.get(word).add(pageEntry);
                    }
                }
            }
        }
    }// прочтите тут все pdf и сохраните нужные данные,
    // тк во время поиска сервер не должен уже читать файлы


    @Override
    public List<PageEntry> search(String words) {
        String[] request = words.toLowerCase().split("\\P{IsAlphabetic}+");
        List<PageEntry> temporaryList = new ArrayList<>();
        List<PageEntry> result = new ArrayList<>();

        for (String newWord : request) {
            if (database.containsKey(newWord)) {
                temporaryList.addAll(database.get(newWord));
                temporaryList.removeAll(stopList);
            }
        }
        Map<String, Map<Integer, Integer>> numberAndQuantity = new HashMap<>();
        for (PageEntry pageEntry : temporaryList) {
            numberAndQuantity.computeIfAbsent(pageEntry.getPdfName(), key -> new HashMap<>())
                    .merge(pageEntry.getPage(), pageEntry.getCount(), Integer::sum);
        }

        numberAndQuantity.forEach((key, value) -> {
            for (var temporaryPage : value.entrySet()) {
                result.add(new PageEntry(key, temporaryPage.getKey(), temporaryPage.getValue()));
            }
        });
        Collections.sort(result);
        return result;
    }
}
