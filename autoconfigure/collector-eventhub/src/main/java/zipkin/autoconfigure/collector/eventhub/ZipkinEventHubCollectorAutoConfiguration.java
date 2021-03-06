/**
 * Copyright 2017 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.autoconfigure.collector.eventhub;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import zipkin.collector.CollectorMetrics;
import zipkin.collector.CollectorSampler;
import zipkin.collector.eventhub.EventHubCollector;
import zipkin.storage.StorageComponent;

@Configuration
@EnableConfigurationProperties(ZipkinEventHubCollectorProperties.class)
@Conditional(ZipkinEventHubCollectorAutoConfiguration.EventHubSetCondition.class)
public class ZipkinEventHubCollectorAutoConfiguration {

  @Bean
  EventHubCollector eventHubCollector(ZipkinEventHubCollectorProperties properties,
      CollectorSampler sampler,
      CollectorMetrics metrics,
      StorageComponent storage) {

    return properties.toBuilder()
        .sampler(sampler)
        .storage(storage)
        .metrics(metrics)
        .build()
        .start();
  }

  static final class EventHubSetCondition extends SpringBootCondition {
    static final String PROPERTY_NAME = "zipkin.collector.eventhub.connection-string";

    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata a) {

      String eventHubProperty = context.getEnvironment().getProperty(PROPERTY_NAME);
      ConditionOutcome outcome = eventHubProperty == null || eventHubProperty.isEmpty() ?
          ConditionOutcome.noMatch(PROPERTY_NAME + " isn't set") :
          ConditionOutcome.match();

      return outcome;
    }
  }
}

