/**
 * This package contains the definitions of Jackson Mixin of the domain model.
 *
 * What is a jackson mixin?
 *
 * Basically are a classes with only contains the same properties names as the domain model classes that contain jackson annotations.
 * This is because Jackson's annotations can be viewed as a detail of the API and not of the business model.
 */
package io.costax.food4u.api.model.mixin;