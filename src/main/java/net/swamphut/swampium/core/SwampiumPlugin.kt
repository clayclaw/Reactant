package net.swamphut.swampium.core

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class SwampiumPlugin(val servicePackages: Array<String>)
