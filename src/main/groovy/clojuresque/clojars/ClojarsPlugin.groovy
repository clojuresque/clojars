/*-
 * Copyright 2013-2014 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Licensed under the EUPL V.1.1 (cf. file EUPL-v1.1 distributed with the
 * source code.) Translations in other european languages available at
 * https://joinup.ec.europa.eu/software/page/eupl.
 *
 * Alternatively, you may choose to use the software under the MIT license
 * (cf.  file MIT distributed with the source code).
 */

package clojuresque.clojars

import org.gradle.api.Plugin
import org.gradle.api.Project

class ClojarsPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: "maven"

        project.extensions.create("clojars", ClojarsExtension, project)

        project.configurations {
            clojuresqueClojarsDeployerJars
        }

        project.dependencies {
            clojuresqueClojarsDeployerJars 'org.apache.maven.wagon:wagon-http-lightweight:2.2'
        }
    }
}
