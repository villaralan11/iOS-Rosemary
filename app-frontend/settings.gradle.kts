pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "iOS-Rosemary-Frontend"

// Módulos:
//  :app     -> Launcher (SpringBoard) + Centro de Control + shaders AGSL de vidrio líquido
//  :xposed  -> Hooks LSPosed/Xposed sobre SystemUI (compileOnly: de.robv.android.xposed:api:82)
include(":app")
include(":xposed")
