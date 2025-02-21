package faang.school.projectservice.config.amazons3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${services.s3.endpoint}")
    private String endpointUrl;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials));

        // Если endpointUrl задан, используем его, иначе стандартный регион
        if (endpointUrl != null && !endpointUrl.isEmpty()) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, null));
            builder.enablePathStyleAccess();  // Включаем для совместимости с MinIO (если это MinIO)
        } else {
            if (region != null && !region.isEmpty()) {
                builder.withRegion(region);
            } else {
                throw new IllegalArgumentException("Region or Endpoint must be specified");
            }
        }

        return builder.build();
    }
}
