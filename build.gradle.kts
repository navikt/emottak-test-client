tasks.register("buildAll") {
    dependsOn(":backend:build", ":frontend:buildFrontend")
}

tasks.register("start") {
    dependsOn(":backend:run", ":frontend:startFrontend")
}

tasks.register("dev") {
    dependsOn(":backend:run", ":frontend:devFrontend")
}