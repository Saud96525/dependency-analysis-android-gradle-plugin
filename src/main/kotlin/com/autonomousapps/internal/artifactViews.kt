package com.autonomousapps.internal

import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.specs.Spec

/**
 * This is different than [org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE], which has type
 * `Category` (cf `String`).
 */
internal val CATEGORY = Attribute.of("org.gradle.category", String::class.java)

private val attributeKey = Attribute.of("artifactType", String::class.java)

internal fun Configuration.artifactsFor(
  attrValue: String,
  filter: Spec<ComponentIdentifier>? = null
): ArtifactCollection = artifactViewFor(attrValue, filter).artifacts

private fun Configuration.artifactViewFor(
  attrValue: String,
  filter: Spec<ComponentIdentifier>? = null
): ArtifactView = incoming.artifactView {
  attributes.attribute(attributeKey, attrValue)
  lenient(true)
//  filter?.let { componentFilter(it) }
  componentFilter { it is ProjectComponentIdentifier }
}

/**
 * Returns true if any of the variants are a kind of platform.
 */
internal fun ResolvedDependencyResult.isJavaPlatform(): Boolean = selected.variants.any { variant ->
  val category = variant.attributes.getAttribute(CATEGORY)
  category == Category.REGULAR_PLATFORM || category == Category.ENFORCED_PLATFORM
}

internal object ArtifactAttributes {
  /** Deprecated. Replaced with [ANDROID_CLASSES_JAR] in AGP 7+. Used only in AGP 4. */
  const val ANDROID_CLASSES_JAR_4 = "android-classes-jar"

  const val ANDROID_CLASSES_JAR = "android-classes"
  const val ANDROID_JNI = "android-jni"
  const val ANDROID_LINT = "android-lint"

  /** This is only available on the _runtime_ classpath. */
  const val ANDROID_ASSETS = "android-assets"
}
