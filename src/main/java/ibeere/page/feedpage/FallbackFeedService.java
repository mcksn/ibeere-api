package ibeere.page.feedpage;

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

import static java.util.Optional.ofNullable;

@Service
public class FallbackFeedService {

    private Storage storage;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${spring.cloud.gcp.project-id}")
    private String googleProjectId;
    @Autowired
    private CredentialsProvider credentialsProvider;

    @PostConstruct
    public void post() throws IOException {
        StorageOptions.Builder options = StorageOptions.newBuilder().
                setProjectId(googleProjectId);

        options.setCredentials(credentialsProvider.getCredentials());

        storage = options.build().getService();
    }

    public void storeFeed() {
        int pageNo = 1;

        ResponseEntity<String> feedPageRes;
        do {
            feedPageRes = restTemplate.getForEntity("https://ibeere.com:8443/page/feed?pageNo=" + pageNo, String.class);

            BlobId blobId = BlobId.of("fallback-feed", pageNo + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
            ofNullable(feedPageRes.getBody()).ifPresent(content -> storage.create(blobInfo, content.getBytes()));
            pageNo++;

        } while (ofNullable(feedPageRes.getBody()).map(d -> d.contains("\"hasMore\":true")).orElse(false));
    }

    public ItemsPage fetch(int pageNo) {

        final HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type", "application.json");

        ResponseEntity<ItemsPage> feedPageRes = restTemplate.exchange("https://storage.googleapis.com/fallback-feed/" + pageNo + ".json",
                HttpMethod.GET, new HttpEntity<>(httpHeaders), ItemsPage.class);

        if (feedPageRes.getBody() != null) {
            return feedPageRes.getBody();
        }
        return null;
    }
}
