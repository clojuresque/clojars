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

import clojuresque.Util

import org.slf4j.LoggerFactory

class ClojarsExtension {
    def private static final logger = LoggerFactory.getLogger(ClojarsExtension);
    def project

    ClojarsExtension(p) {
        project = p
    }

    void repo(repositories) {
        repositories.maven { url "http://clojars.org/repo" }
    }

    def static final repositoryHosts = [
        github: { repo ->
            return [
                url:     "https://github.com/${repo}",
                repo:    "scm:git:git://github.com/${repo}.git",
                devRepo: "scm:git:ssh://git@github.com/${repo}.git"
            ]
        },
        bitbucket: { repo ->
            return [
                url:     "https://bitbucket.org/${repo}",
                repo:    "scm:hg:https://bitbucket.org/${repo}",
                devRepo: "scm:hg:ssh://hg@bitbucket.org/${repo}"
            ]
        }
    ]

    def static repositoryConfig(repo, flavor) {
        if (flavor instanceof Closure)
            return flavor(repo)

        def repoConfig = repositoryHosts.get(flavor, null)
        if (repoConfig != null)
            return repoConfig(repo)

        throw new IllegalArgumentException("Don't know repo flavor: ${flavor}")
    }

    def final static openSourceLicenses = [
        "AFL-3.0":      "Academic Free License 3.0",
        "APL-1.0":      "Adaptive Public License",
        "Apache-2.0":   "Apache License 2.0",
        "APSL-2.0":     "Apple Public Source License",
        "Artistic-2.0": "Artistic license 2.0",
        "AAL":          "Attribution Assurance Licenses",
        "BSD-3-Clause": "Revised BSD License",
        "BSD-2-Clause": "FreeBSD License",
        "BSL-1.0":      "Boost Software License",
        "CECILL-2.1":   "CeCILL License 2.1",
        "CATOSL-1.1":   "Computer Associates Trusted Open Source License 1.1",
        "CDDL-1.0":     "Common Development and Distribution License 1.0",
        "CPAL-1.0":     "Common Public Attribution License 1.0",
        "CUA-OPL-1.0":  "CUA Office Public License Version 1.0",
        "EUDatagrid":   "EU DataGrid Software License",
        "EPL-1.0":      "Eclipse Public License 1.0",
        "ECL-2.0":      "Educational Community License, Version 2.0",
        "EFL-2.0":      "Eiffel Forum License V2.0",
        "Entessa":      "Entessa Public License",
        "EUPL-1.1":     "European Union Public License, Version 1.1",
        "Fair":         "Fair License",
        "Frameworx-1.0": "Frameworx License",
        "AGPL-3.0":     "GNU Affero General Public License v3",
        "GPL-2.0":      "GNU General Public License version 2.0",
        "GPL-3.0":      "GNU General Public License version 3.0",
        "LGPL-2.1":     "GNU Library General Public License version 2.1",
        "LGPL-3.0":     "GNU Library General Public License version 3.0",
        "HPND":         "Historical Permission Notice and Disclaimer",
        "IPL-1.0":      "IBM Public License 1.0",
        "IPA":          "IPA Font License",
        "ISC":          "ISC License",
        "LPPL-1.3c":    "LaTeX Project Public License 1.3c",
        "LPL-1.02":     "Lucent Public License Version 1.02",
        "MirOS":        "MirOS Licence",
        "MS-PL":        "Microsoft Public License",
        "MS-RL":        "Microsoft Reciprocal License",
        "MIT":          "MIT license",
        "Motosoto":     "Motosoto License",
        "MPL-2.0":      "Mozilla Public License 2.0",
        "Multics":      "Multics License",
        "NASA-1.3":     "NASA Open Source Agreement 1.3",
        "NTP":          "NTP License",
        "Naumen":       "Naumen Public License",
        "NGPL":         "Nethack General Public License",
        "Nokia":        "Nokia Open Source License",
        "NPOSL-3.0":    "Non-Profit Open Software License 3.0",
        "OCLC-2.0":     "OCLC Research Public License 2.0",
        "OFL-1.1":      "Open Font License 1.1",
        "OGTSL":        "Open Group Test Suite License",
        "OSL-3.0":      "Open Software License 3.0",
        "PHP-3.0":      "PHP License 3.0",
        "PostgreSQL":   "The PostgreSQL License",
        "Python-2.0":   "Python License",
        "CNRI-Python":  "CNRI Python license",
        "QPL-1.0":      "Q Public License",
        "RPSL-1.0":     "RealNetworks Public Source License V1.0",
        "RPL-1.5":      "Reciprocal Public License 1.5",
        "RSCPL":        "Ricoh Source Code Public License",
        "SimPL-2.0":    "Simple Public License 2.0",
        "Sleepycat":    "Sleepycat License",
        "SPL-1.0":      "Sun Public License 1.0",
        "Watcom-1.0":   "Sybase Open Watcom Public License 1.0",
        "NCSA":         "University of Illinois/NCSA Open Source License",
        "VSL-1.0":      "Vovida Software License v. 1.0",
        "W3C":          "W3C License",
        "WXwindows":    "wxWindows Library License",
        "Xnet":         "X.Net License",
        "ZPL-2.0":      "Zope Public License 2.0",
        "Zlib":         "zlib/libpng license"
    ].collectEntries { license, title ->
        [ license,
          [ name: "The ${title}",
            url:  "http://opensource.org/licenses/${license}" ]]
    }

    def static licenseConfig(license) {
        if (license instanceof Map)
            return license

        def licenseConfig = openSourceLicenses.get(license, null)
        if (licenseConfig != null)
            return licenseConfig

        throw new IllegalArgumentException("Unknown license: ${license}")
    }

    void deployStandard(Map options=[:], upload) {
        def repoConfig = repositoryConfig(options.repository, options.repositoryFlavor)
        def licConfigs = options.licenses.collect { licenseConfig(it) }
        def projectUrl = options.get("url", repoConfig.url)

        deployCustom(options.get("credentials", [:]), upload) {
            project {
                licenses {
                    licConfigs.each { lic ->
                        license {
                            name lic.name
                            url  lic.url
                            distribution 'repo'
                        }
                    }
                }
                description project.description
                url projectUrl
                scm {
                    connection repoConfig.repo
                    developerConnection repoConfig.devRepo
                    url repoConfig.url
                }
            }
        }
    }

    def configureCredentials(Map creds=[:]) {
        def defaults = [
            username:     null,
            password:     null,
            url:          "https://clojars.org/repo",
            snapshotsUrl: null
        ]

        if (project.hasProperty("clojuresque.clojars.username"))
            defaults.username = project["clojuresque.clojars.username"]

        if (project.hasProperty("clojuresque.clojars.password"))
            defaults.password = project["clojuresque.clojars.password"]

        if (project.hasProperty("clojuresque.clojars.url"))
            defaults.url = project["clojuresque.clojars.url"]

        if (project.hasProperty("clojuresque.clojars.snapshotsUrl"))
            defaults.snapshotsUrl = project["clojuresque.clojars.snapshotsUrl"]

        defaults.plus(creds)
    }

    void deploy(Map cs=[:], upload, Closure projectSpec={}) {
        Util.deprecationWarning(logger, "deploy()", "deployCustom()")
        deployCustom(cs, upload, projectSpec)
    }

    void deployCustom(Map credentials=[:], upload, Closure projectSpec) {
        def creds = configureCredentials(credentials)

        if (creds.username == null) {
            upload.enabled = false
            return
        }

        if (creds.password == null) {
            upload.enabled = false
            return
        }

        project.afterEvaluate {
            upload.repositories {
                mavenDeployer {
                    configuration =
                        project.configurations.clojuresqueClojarsDeployerJars
                    repository(url: chooseUrl(isSnapshot(project.version), creds)) {
                        authentication(
                            userName: creds.username,
                            password: creds.password
                        )
                    }
                    project.configure(pom, projectSpec)
                    if (project.hasProperty("signing")) {
                        beforeDeployment { project.signing.signPom(it) }
                    }
                }
            }
        }
    }

    static isSnapshot(v)   { v?.endsWith("-SNAPSHOT") }
    static chooseUrl(s, c) { s ? (c.snapshotsUrl ?: c.url) : c.url }
}
