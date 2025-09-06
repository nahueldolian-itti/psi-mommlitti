package com.psi.booking.infrastructure.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class AwsConfiguration {

    @Value("\${aws.region}")
    private lateinit var region: String

    @Value("\${aws.dynamodb.endpoint:}")
    private lateinit var dynamoDbEndpoint: String

    @Value("\${aws.sqs.endpoint:}")
    private lateinit var sqsEndpoint: String

    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        val builder = DynamoDbClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())

        if (dynamoDbEndpoint.isNotEmpty()) {
            builder.endpointOverride(URI.create(dynamoDbEndpoint))
        }

        return builder.build()
    }

    @Bean
    fun snsClient(): SnsClient {
        return SnsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }

    @Bean
    fun sqsClient(): SqsClient {
        val builder = SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())

        if (sqsEndpoint.isNotEmpty()) {
            builder.endpointOverride(URI.create(sqsEndpoint))
        }

        return builder.build()
    }
}
