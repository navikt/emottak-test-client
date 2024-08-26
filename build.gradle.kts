tasks.register("buildAll") {
    dependsOn(":backend:build", ":frontend:buildFrontend")
}

tasks.register("buildBackend") {
    dependsOn(":backend:build")
}

tasks.register("buildFrontend") {
    dependsOn(":frontend:buildFrontend")
}

tasks.register("start") {
    dependsOn(":backend:run", ":frontend:startFrontend")
}

tasks.register("dev") {
    dependsOn(":backend:run", ":frontend:devFrontend")
}

tasks.register("backend") {
    dependsOn(":backend:run")
}

tasks.register("frontend") {
    dependsOn(":frontend:devFrontend")
}

