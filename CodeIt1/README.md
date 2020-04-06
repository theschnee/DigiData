This is the updated code that should cover the edge cases for the quiz app made during class over week 1. 
All of the code was modified off of the base code that was given to us by Professor Campbell friday.

I believe that the 4th edge case was that if you pressed the buttons twice quickly in some order on the same question, the boolean would reset and show you that you did not cheat again.
I stumbled upon this by accident while messing with the base version given by Professor Campbell.
To solve this I originally expanded the declaration of the question structure to include a boolean for cheating, however i then abandoned this strategy and made a sister array of the question array in QuizActivity.
I did this because I could not find a way to store the question array in between states, and there is a built in method for boolean arrays.
You will see that the question structure is modified with the additional functionality of a cheated boolean (and getters and setters) even though it is not used.
I left them in because question still works normally with them there and at some opint i would like to learn and mess around with saving customized classes between states. 

If there are any problems running the code please let me know, I didn't know if you wanted just the java files for the project or everything compiled too. 


