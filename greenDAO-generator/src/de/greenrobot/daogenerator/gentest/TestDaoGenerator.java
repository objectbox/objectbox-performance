/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daogenerator.gentest;

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
        createSimple();
        createSimpleNotNull();
    }

    public void generate() throws Exception {
        DaoGenerator daoGenerator = new DaoGenerator();
        String src = "../app/src/main/java/";
        daoGenerator.generateAll(schema, src);
    }

    protected void createSimple() {
        Entity simple = schema.addEntity("SimpleEntity");
        addSimpleProperties(simple);
    }

    private PropertyBuilder addSimpleProperties(Entity entity) {
        entity.addIdProperty();
        entity.addBooleanProperty("simpleBoolean");
        entity.addByteProperty("simpleByte");
        entity.addShortProperty("simpleShort");
        entity.addIntProperty("simpleInt");
        entity.addLongProperty("simpleLong");
        entity.addFloatProperty("simpleFloat");
        entity.addDoubleProperty("simpleDouble");
        PropertyBuilder simpleString = entity.addStringProperty("simpleString");
        entity.addByteArrayProperty("simpleByteArray");
        return simpleString;
    }

    protected void createSimpleNotNull() {
        Entity notNull = schema.addEntity("SimpleEntityNotNull");
        notNull.addIdProperty().notNull();
        notNull.addBooleanProperty("simpleBoolean").notNull();
        notNull.addByteProperty("simpleByte").notNull();
        notNull.addShortProperty("simpleShort").notNull();
        notNull.addIntProperty("simpleInt").notNull();
        notNull.addLongProperty("simpleLong").notNull();
        notNull.addFloatProperty("simpleFloat").notNull();
        notNull.addDoubleProperty("simpleDouble").notNull();
        notNull.addStringProperty("simpleString").notNull();
        notNull.addByteArrayProperty("simpleByteArray").notNull();
    }


}
