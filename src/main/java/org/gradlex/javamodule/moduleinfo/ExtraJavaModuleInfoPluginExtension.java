/*
 * Copyright 2022 the GradleX team.
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

package org.gradlex.javamodule.moduleinfo;

import org.gradle.api.Action;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import javax.annotation.Nullable;

/**
 * A data class to collect all the module information we want to add.
 * Here the class is used as extension that can be configured in the build script
 * and as input to the ExtraModuleInfoTransform that add the information to Jars.
 */
@SuppressWarnings("unused")
public abstract class ExtraJavaModuleInfoPluginExtension {

    abstract public MapProperty<String, ModuleSpec> getModuleSpecs();
    abstract public Property<Boolean> getFailOnMissingModuleInfo();

    /**
     * Add full module information for a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
     */
    public void module(String identifier, String moduleName) {
        module(identifier, moduleName, null, null);
    }

    /**
     * Add full module information for a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
    *  @param moduleVersion version to write into the module-info.class
     */
    public void module(String identifier, String moduleName, String moduleVersion) {
        module(identifier, moduleName, moduleVersion, null);
    }

    /**
     * Add full module information for a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
     * @param conf configure exported packages and dependencies, see {@link ModuleInfo}
     */
    public void module(String identifier, String moduleName, @Nullable Action<? super ModuleInfo> conf) {
        module(identifier, moduleName, null, conf);
    }

    /**
     * Add full module information for a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
     * @param moduleVersion version to write into the module-info.class
     * @param conf configure exported packages, dependencies and Jar merging, see {@link ModuleInfo}
     */
    public void module(String identifier, String moduleName, @Nullable String moduleVersion, @Nullable Action<? super ModuleInfo> conf) {
        ModuleInfo moduleInfo = new ModuleInfo(identifier, moduleName, moduleVersion);
        if (conf != null) {
            conf.execute(moduleInfo);
        }
        this.getModuleSpecs().put(identifier, moduleInfo);
    }

    /**
     * Add an Automatic-Module-Name to a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
     */
    public void automaticModule(String identifier, String moduleName) {
        automaticModule(identifier, moduleName, null);
    }

    /**
     * Add an Automatic-Module-Name to a given Jar file.
     *
     * @param identifier group:name coordinates _or_ Jar file name
     * @param moduleName the Module Name of the Module to construct
     * @param conf configure Jar merging, see {@link AutomaticModuleName}
     */
    public void automaticModule(String identifier, String moduleName, @Nullable Action<? super AutomaticModuleName> conf) {
        AutomaticModuleName automaticModuleName = new AutomaticModuleName(identifier, moduleName);
        if (conf != null) {
            conf.execute(automaticModuleName);
        }
        getModuleSpecs().put(identifier, automaticModuleName);
    }

    /**
     * Let the plugin know about an existing module on the module path.
     * This may be needed when 'requiresDirectivesFromMetadata(true)' is used.
     *
     * @param coordinates group:name coordinates
     * @param moduleName the Module Name of the Module referred to by the coordinates
     */
    public void knownModule(String coordinates, String moduleName) {
        getModuleSpecs().put(coordinates, new KnownModule(coordinates, moduleName));
    }
}
