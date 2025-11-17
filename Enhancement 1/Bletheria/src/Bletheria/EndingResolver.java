package Bletheria;

/**
 * The EndingResolver handles different endgame outcomes
 * based on items collected and whether the player found the secret key.
 */
public class EndingResolver {
    public String getEndingText(Player p) {
        boolean hasKey = p.hasItem("Key");
        int totalItems = p.getInventory().size();

        // Check if player is in boss room and return appropriate ending
        if ("Demon High Temple".equals(p.getCurrentRoom())) {
            if (totalItems >= 6 && hasKey) {
                return secretEnding();
            } else if (totalItems >= 6) {
                return goodEnding();
            } else {
                return badEnding();
            }
        }
        return "";
    }

    // Secret "evil" ending text
    private String secretEnding() {
        return """
        CONGRATULATIONS!! You have vanquished the evil wizard!\n 
        \nI see that you have the secret key to unlock High Demon Wisdom, please enter the well 
        of wisdom. 
        \nAs you enter the well everything around you fades to black and suddenly you are floating in what seems like 
        space surrounded by stars and galaxies.  
        \nIn front of you floats a vial.  
        A disembodied voice whispers for you to drink it.  
        \nYou don’t know what compels you to, but without thinking you lurch forward, grab the vial, 
        and drink it in its entirety.  
        You feel a power surge through you as well as an anger you have never felt before.\n  
        Darkness spreads from your fingertips to your elbows.  
        Unholy sigils are seared into your flesh.  
        The disembodied voice starts laughing and Nozgorath appears in ghostly form.\n  
        He laughs and welcomes you to your new reign as ruler of the Realm of Misfortune.  
        Your soul belongs to the well of wisdom now, however, your power knows no bounds and 
        you are granted immortality.
        You should be upset, but an evil smile spreads across your face.  
        You take your seat on the throne of misfortune in the High Demon Temple.
        \nALL HAIL THE NEW RULER OF THE REALM!!! \nALL HAIL THE DECREPIT ONE!\n
            (Type 'exit' to end game.)
        """;
    }

    // Standard good ending text
    private String goodEnding() {
        return """
You have vanquished Nozgorath and avenged your master!
You leave the Realm of Misfortune with your new title as the Demon Slayer Mage.
(Type 'exit' to end game.)
        """;
    }

    // Bad ending text if underprepared
    private String badEnding() {
        return """
As you enter the temple, Nozgorath laughs as he sees your unprepared state.
the Demon Lord looks at you with his blackened eyes and you spontaneously combust,.
(Type 'exit' to end game.)
        """;
    }
}
