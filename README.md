# CARP

## TODO

1. Implement direct assignment ability scores generation
    1. Add a user input to choose ability generation method
    2. When selecting direct generation, make ability score editable
    3. Bind ability modifier to ability score using static method from AbilityScore
    4. Add button to validate
    5. Update creature's ability scores
1. Merge with master and apply code review feedback
1. Implement standard ability scores generation
    1. Add new choice to user input for method selection
    2. Add global parameter deciding if user wants to be asked to roll dice, 
    let the system do it and check result, 
    or let the system do it and accept results
    3. Implement view/viewModel for dice rolls (listview of listview) :
        1. For each roll object, 2 buttons : roll and input results
        2. Three global buttons: roll, input results and validate (disabled until all dice have a result)
        3. Add option for each dice to keep only n-highest/lowest dice results (if input results, disable discarded dice)
    4. Implement view/viewModel for generation : 
        1. new window with ability scores view, view for dice rolls, a way to link them and a validate button
        2. Upon validation, update creature's ability scores
1. Implement dice pool ability scores generation
1. Redesign and implement bonus type
    1. Check impact on dice rolls (model, viewmodel, view)
