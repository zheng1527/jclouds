/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rds.features;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.internal.BaseRDSApiLiveTest;
import org.jclouds.rds.options.ListInstancesOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseRDSApiLiveTest {

   private void checkInstance(Instance instance) {
      checkNotNull(instance.getId(), "Id cannot be null for a Instance: %s", instance);
      checkNotNull(instance.getCreatedTime(), "CreatedTime cannot be null for a Instance: %s", instance);
      checkNotNull(instance.getName(), "While Name can be null for a Instance, its Optional wrapper cannot: %s",
               instance);
      checkNotNull(instance.getSubnetGroup(),
               "While SubnetGroup can be null for a Instance, its Optional wrapper cannot: %s", instance);
      // TODO: other checks
      if (instance.getSubnetGroup().isPresent())
         SubnetGroupApiLiveTest.checkSubnetGroup(instance.getSubnetGroup().get());
   }

   @Test
   protected void testDescribeInstances() {
      IterableWithMarker<Instance> response = api().list().get(0);

      for (Instance instance : response) {
         checkInstance(instance);
      }

      if (Iterables.size(response) > 0) {
         Instance instance = response.iterator().next();
         Assert.assertEquals(api().get(instance.getId()), instance);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListInstancesOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (Instance instance : response) {
         checkInstance(instance);
      }
   }

   protected InstanceApi api() {
      return context.getApi().getInstanceApi();
   }
}
