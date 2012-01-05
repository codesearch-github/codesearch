`Please feel free to add items to this list!`

# Missing features
 * Extended file information: e.g Author, Commit date etc
 * Jump between found results
 * Repository file browsing
 * Raw file button
 * Display file without code navigation button
 * Autocomplete

# Important
 * When the error message display is finished, refactor searcher error handling
   to actually use it!
 * Find out whether the Repository Groups in the configuration are really necessary

# Bugs
 * XmlCodeAnalyzer takes reaaallly long to analyze some files
 * Either find a way to preserve \n chars at parsing or completely replace the JavaParser with the EclipseAstParser

# Minor Bugs
 * usages of parameters in methods are not correctly linked

# Unexpected Behaviour
 * When using an overloaded method with an external field or external method as parameter it can not be correctly determined which method is the correct one
