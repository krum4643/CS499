import os


# clears screen
def clear():
    os.system('cls' if os.name == 'nt' else 'clear')


# deletes secret cavern to lock secret ending
def no_secret_for_you():
    try:
        del (unfortunates['Forgotten Hut of Secret Spaces']['East'])
        del (unfortunates['Secret Cavern'])
    except KeyError:
        pass


# intro and setup with instructions
def main_menu():
    print('Welcome to Bletheria!')
    print("""Your master was murdered by the High Demon Wizard Nozgorath the Decrepit.  
You must make your way through the lower demon city of Black Bletheria in The Realm of Misfortune, 
to the Demon's high temple to take your revenge, avenge your master, and try not to let 
the darkness consume you along the way.""")
    print()
    print("""In order to exact your revenge, you must collect the following:
    - The Rare Charred Demon Wand to vanquish Nozgorath, 
    - The Invisibility Cloak to hide your intentions, 
    - The Dark Potion of Bravery to steady your nerves, 
    - The Spellbook of the Dead and Withered to bolster your magic prowess, 
    - The Demon Grieves of Wiwaria Common to protect your soul, 
    - The Mask of Intuition to increase your perception, 
    - Finally, if you are high in luck, the Secret Key to unlock High Demon Wisdom """)
    print()
    adventurer_name = input('What is your name, adventurer? ')
    clear()
    print(adventurer_name, """the Lowly! Welcome to The Realm of Misfortune, 
    \nNozgorath the Decrepit awaits you in the High Temple, don’t keep him waiting!""")
    print("""To move between rooms, use the directional commands North, South, East, and West. 
    \nUse the command 'travel' before each directional command when prompted. 
    \nTo add an item to your inventory, use the command 'equip' followed by the item name.\n""")

    # set decision to unlock secret ending or not
    wisdom_or_perception = input('Before you head out on your adventure, will you buff Wisdom or Perception: \n')

    perk_choice = wisdom_or_perception.split(' ')

    wizard_attribute = perk_choice[0].title()

    if wizard_attribute == 'Wisdom':
        print('\nExcellent choice wizard, wise you shall be\n')
        no_secret_for_you()

    elif wizard_attribute == 'Perception':
        print('\nInteresting choice wizard, the Realm of Misfortune has many secrets, keep your eyes peeled.\n')

    else:
        print('Invalid command')


# dictionary of connecting rooms with items
unfortunates = {
    'Ashen Foyer': {'South': 'The Acrid Swamp', 'North': 'Wiwaria Common',
                    'East': 'The Haunted Library of Wayward Souls', 'West': 'Rotten Forrest'},
    'The Acrid Swamp': {'North': 'Ashen Foyer', 'East': 'Cursed Workshop of Azazel the Ensnared',
                        'item': 'Potion'},
    'Cursed Workshop of Azazel the Ensnared': {'West': 'The Acrid Swamp', 'item': 'Mask'},
    'Rotten Forrest': {'East': 'Ashen Foyer', 'item': 'Wand'},
    'Wiwaria Common': {'South': 'Ashen Foyer', 'East': 'Forgotten Hut of Secret Spaces', 'item': 'Grieves'},
    'Forgotten Hut of Secret Spaces': {'West': 'Wiwaria Common', 'East': 'Secret Cavern', 'item': 'Cloak'},
    'Secret Cavern': {'West': 'Forgotten Hut of Secret Spaces', 'item': 'Key'},
    'The Haunted Library of Wayward Souls': {'West': 'Ashen Foyer', 'North': 'Demon High Temple', 'item':
        'Spellbook'},
    'Demon High Temple': {'Boss': 'Nozgorath the Decrepit'}
}

# list for storing player inventory
inventory = []

# starting room
current_room = "Ashen Foyer"

# show the player the main menu and then clear screen
main_menu()

# last move
msg = ""

# Gameplay loop
while True:

    print("""To move between rooms, use the directional commands North, South, East, and West. 
        \nUse the command 'travel' before each directional command when prompted.\n
        \nTo add an item to your inventory, use the command 'equip' followed by the item name.\n""")

    # Display player info
    print(f"You are in the {current_room}\n\nInventory : {inventory}\n{'-' * 30}\n")

    # Display msg
    print(msg)

    # Item indicator
    if 'item' in unfortunates[current_room].keys():

        nearby_item = unfortunates[current_room]["item"]

        if nearby_item not in inventory:
            print(f"You see the {nearby_item} on a rotten pedestal\n")

    # Boss encounter
    if "Boss" in unfortunates[current_room].keys():

        if len(inventory) >= 6 and 'Key' in inventory:
            print("""CONGRATULATIONS!! You have vanquished the evil wizard!\n 
        \nI see that you have the secret key to unlock High Demon Wisdom, please enter the well 
        of wisdom. 
        \nAs you enter the well everything around you fades to black and suddenly you are floating in what seems like 
        \nspace surrounded by stars and galaxies.\n  
        \nIn front of you floats a vial.  
        \nA disembodied voice whispers for you to drink it.\n  
        \nYou don’t know what compels you to, but without thinking you lurch forward, grab the vial, 
        \nand drink it in its entirety.  You feel a power surge through you as well as an anger you 
        \nhave never felt before.\n  
        \nDarkness spreads from your fingertips to your elbows.  
        \nUnholy sigils are seared into your flesh.  The disembodied voice starts laughing and
        \nNozgorath appears in ghostly form.\n  
        \nHe laughs and welcomes you to your new reign as ruler 
        \nof the Realm of Misfortune.  
        \nYour soul belongs to the well of wisdom now, however, your power knows no bounds and 
        \nyou are granted immortality.\nYou should be upset, but an evil smile spreads across your face.  
        \nYou take your seat on the throne 
        \nof misfortune in the High Demon Temple.
        \nALL HAIL THE NEW RULER OF THE REALM!!! \nALL HAIL THE DECREPIT ONE!\n""")
            print('You have found the secret ending! Thank you for playing!\n\n')
            print('Type exit to end game')

        elif len(inventory) >= 6 and 'Key' not in inventory:
            print("""CONGRATULATIONS!!!! You have vanquished Nozgorath and have avenged your master.
            \nYou leave the Realm of Misfortune with your soul and the title of Demon Slayer Mage!\n""")
            print('Thank you for playing!\n\n')
            print('Type exit to end game')

        # losing case
        else:
            print('\nNozgorath looks upon you from his throne with disgust and disappointment, he shouts,'
                  '\n"Oh dear! You have come unprepared to face me! such a shame"\n')
            print('the Demon Lord looked at you with his blackened eyes and you spontaneously combust,'
                  '\nbetter luck next time!\n')
            print('Thank you for playing!\n\n')
            print('Type exit to end game')

    # Accepts player's move as input
    user_input = input("Enter your move:\n")

    clear()

    # Splits move into words
    next_move = user_input.split(' ')

    # First word is action
    action = next_move[0].title()

    # Reset item and direction
    item = 'item'
    direction = 'null'

    # Second word is object or direction
    if len(next_move) > 1:
        item = next_move[1:]
        direction = next_move[1].title()

        item = " ".join(item).title()

    # Moving between rooms
    if action == "Travel":

        try:
            current_room = unfortunates[current_room][direction]
            msg = f"You travel {direction}\n"

        except:
            msg = "You can't go that way.\n"

    # Picking up items
    elif action == "Equip":
        try:
            if item == unfortunates[current_room]['item']:

                if item not in inventory:

                    inventory.append(unfortunates[current_room]['item'])
                    msg = f"{item} equipped!\n"

                else:
                    msg = f"You already have the {item}\n"

            else:
                msg = f"Can't find {item}\n"
        except:
            msg = f"Can't find {item}\n"

    # Exit program
    elif action == "Exit":
        break

    # Any other commands invalid
    else:
        msg = "Invalid command\n"