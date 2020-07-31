package dev.reactant.reactant.core.dependency.layers

/**
 * Implement this interface will declare that your component is a SystemLevel component
 * Therefore all functionality level component will implicit depends on it
 *
 * Remind that a SystemLevel component can only depends on other SystemLevel component
 * Otherwise due the the rule of functionality level component, it will cause cyclic dependency exception
 */
interface SystemLevel
