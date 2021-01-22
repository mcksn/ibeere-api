package ibeere.page.faqpage;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ibeere.page.common.ItemsPage;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.google.cloud.storage.StorageOptions.newBuilder;
import static java.util.Optional.ofNullable;

@Service
public class FallbackFAQService {

    private Storage storage;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${spring.cloud.gcp.project-id}")
    private String googleProjectId;
    @Autowired
    private CredentialsProvider credentialsProvider;

    @PostConstruct
    public void post() throws IOException {
        StorageOptions.Builder options = newBuilder().
                setProjectId(googleProjectId)
                .setCredentials(credentialsProvider.getCredentials());

        storage = options.build().getService();
    }

    public void storeFAQ() {
        ResponseEntity<String> faqPageRes;
        faqPageRes = restTemplate.getForEntity("https://192.168.0.17:8080/page/faq", String.class);
        BlobId blobId = BlobId.of("fallback-faq",  "1.json");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
        ofNullable(faqPageRes.getBody()).ifPresent(content -> storage.create(blobInfo, content.getBytes()));
    }

    public ItemsPage fetch() {

        final HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type", "application.json");

        ResponseEntity<ItemsPage> faqPageRes = restTemplate.exchange("https://storage.googleapis.com/fallback-faq/1.json",
                HttpMethod.GET, new HttpEntity<>(httpHeaders), ItemsPage.class);

        if (faqPageRes.getBody() != null) {
            return faqPageRes.getBody();
        }
        return null;
    }
}
