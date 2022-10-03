
namesCount = dict()

# Calculations
with open('C:/Users/DanielaDias/Documents/Uni/CBD/Lab-1/names.txt', 'r') as names:
    line = names.readline()
    while line != '':

        letter = line[0].upper()
        if letter not in namesCount:
            namesCount[letter] = 1
        else:
            namesCount[letter] = namesCount[letter] + 1

        line = names.readline()

# Write names_counting.text
f = open('C:/Users/DanielaDias/Documents/Uni/CBD/Lab-1/names_counting.txt', 'w')
for letter in namesCount:
    f.write("SET {} {}\n".format(letter, namesCount[letter]))
    print("SET {} {}".format(letter, namesCount[letter]))
f.close()

