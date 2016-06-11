/**
 * Copyright 2016 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.soabase.halva.caseclass;

import com.company.JsonTest;
import com.company.JsonTestCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

public class TestJson
{
    @Test
    public void testFromJson() throws IOException
    {
        String json = "{"
            + "\"firstName\": \"John\","
            + "\"lastName\": \"Galt\","
            + "\"age\": 42"
            + "}";
        JsonTest deserialized = new ObjectMapper().readerFor(JsonTestCase.class).readValue(json);
        Assert.assertEquals(JsonTestCase.builder().firstName("John").lastName("Galt").age(42).build(), deserialized);
    }

    @Test
    public void testToJson() throws IOException
    {
        JsonTestCase object = JsonTestCase.builder().firstName("John").lastName("Galt").age(42).build();
        String json = new ObjectMapper().writeValueAsString(object);
        JsonTest deserialized = new ObjectMapper().readerFor(JsonTestCase.class).readValue(json);
        Assert.assertEquals(object, deserialized);
    }
}
