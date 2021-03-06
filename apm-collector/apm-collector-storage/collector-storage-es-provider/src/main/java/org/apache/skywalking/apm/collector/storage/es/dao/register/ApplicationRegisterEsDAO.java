/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.collector.storage.es.dao.register;

import java.util.HashMap;
import java.util.Map;
import org.apache.skywalking.apm.collector.client.elasticsearch.ElasticSearchClient;
import org.apache.skywalking.apm.collector.storage.dao.register.IApplicationRegisterDAO;
import org.apache.skywalking.apm.collector.storage.es.base.dao.EsDAO;
import org.apache.skywalking.apm.collector.storage.table.register.Application;
import org.apache.skywalking.apm.collector.storage.table.register.ApplicationTable;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng-yongsheng
 */
public class ApplicationRegisterEsDAO extends EsDAO implements IApplicationRegisterDAO {

    private final Logger logger = LoggerFactory.getLogger(ApplicationRegisterEsDAO.class);

    public ApplicationRegisterEsDAO(ElasticSearchClient client) {
        super(client);
    }

    @Override public int getMaxApplicationId() {
        return getMaxId(ApplicationTable.TABLE, ApplicationTable.APPLICATION_ID.getName());
    }

    @Override public int getMinApplicationId() {
        return getMinId(ApplicationTable.TABLE, ApplicationTable.APPLICATION_ID.getName());
    }

    @Override public void save(Application application) {
        logger.debug("save application register info, application getApplicationId: {}, application code: {}", application.getId(), application.getApplicationCode());
        ElasticSearchClient client = getClient();
        Map<String, Object> target = new HashMap<>();
        target.put(ApplicationTable.APPLICATION_CODE.getName(), application.getApplicationCode());
        target.put(ApplicationTable.APPLICATION_ID.getName(), application.getApplicationId());
        target.put(ApplicationTable.ADDRESS_ID.getName(), application.getAddressId());
        target.put(ApplicationTable.IS_ADDRESS.getName(), application.getIsAddress());

        IndexResponse response = client.prepareIndex(ApplicationTable.TABLE, application.getId()).setSource(target).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get();
        logger.debug("save application register info, application getApplicationId: {}, application code: {}, status: {}", application.getApplicationId(), application.getApplicationCode(), response.status().name());
    }
}
