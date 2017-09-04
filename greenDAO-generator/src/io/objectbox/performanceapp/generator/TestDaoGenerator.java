/*
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.objectbox.performanceapp.generator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property.PropertyBuilder;
import org.greenrobot.greendao.generator.Schema;

/**
 * Generates test entities for test project DaoTest.
 *
 * @author Markus
 */
public class TestDaoGenerator {

    public static void main(String[] args) throws Exception {
        TestDaoGenerator testDaoGenerator = new TestDaoGenerator();
        testDaoGenerator.generate();
    }

    private final Schema schema;

    public TestDaoGenerator() {
        schema = new Schema(1, "io.objectbox.performanceapp.greendao");
        createSimple("SimpleEntity", false);
        createSimple("SimpleEntityIndexed", true);
    }

    public void generate() throws Exception {
        DaoGenerator daoGenerator = new DaoGenerator();
        String src = "../app/src/main/java/";
        daoGenerator.generateAll(schema, src);
    }

    protected void createSimple(String name, boolean indexed) {
        Entity notNull = schema.addEntity(name);
        notNull.addIdProperty().notNull();
        notNull.addBooleanProperty("simpleBoolean").notNull();
        notNull.addByteProperty("simpleByte").notNull();
        notNull.addShortProperty("simpleShort").notNull();
        notNull.addIntProperty("simpleInt").notNull();
        notNull.addLongProperty("simpleLong").notNull();
        notNull.addFloatProperty("simpleFloat").notNull();
        notNull.addDoubleProperty("simpleDouble").notNull();
        PropertyBuilder simpleString = notNull.addStringProperty("simpleString");
        if(indexed) {
            simpleString.index();
        }
        notNull.addByteArrayProperty("simpleByteArray");
    }


}
