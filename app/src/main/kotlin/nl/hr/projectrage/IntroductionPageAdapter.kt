package nl.hr.projectrage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_introduction.view.*
import nl.hr.projectrage.IntroductionPageAdapter.ViewHolder

class IntroductionPageAdapter : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_introduction, parent, false) as ViewGroup)

    override fun getItemCount() = 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> holder.view.apply {
                circleTextView.text = "Welkom bij ProjectRage"
                bodyText.text =
                    """
                        Voel je je wel eens onveilig op straat?
                        
                        Zou je de mogelijkheid willen hebben om, als je op straat wordt belaagd, door het roepen van een codewoord om hulp zou kunnen vragen?
                        
                        Met deze app zoeken we uit of dat een mogelijkheid zou kunnen zijn.
                    """.trimIndent()
                imageView.setImageResource(R.drawable.bg_introduction0)
                imageViewIcon.setImageResource(R.drawable.ic_maxedguardian)
            }
            1 -> holder.view.apply {
                circleTextView.text = "Wat is ProjectRage?"
                bodyText.text ="""
                    ProjectRage is een experimentele app om uit te zoeken wat goede codewoorden zijn om te gebruiken.
                    
                    Een goed codewoord is altijd goed herkenbaar als het wordt geroepen, maar geeft geen vals alarm tijdens een normaal gesprek.
                """.trimIndent()
                imageView.setImageResource(R.drawable.bg_introduction1)
            }
            2 -> holder.view.apply {
                circleTextView.text = "Hoe werkt ProjectRage?"
                bodyText.text = """
                    Bij de instellingen kan je je eigen codewoord invoeren.
                    
                    Bij het invoeren krijg je een score of het volgens ons algoritme een goed codewoord zou zijn of niet.
                    
                    Vervolgens kan je testen of deze score goed zou zijn door op de hoofdpagina in verschillende situaties (bijvoorbeeld vanuit je broekzak, op een paar meter afstand, in harde wind, etc) uit te proberen, en kijken of het inderdaad herkend wordt of niet.
                """.trimIndent()
                imageView.setImageResource(R.drawable.bg_introduction2)
            }
        }
    }

    inner class ViewHolder(val view: ViewGroup) : RecyclerView.ViewHolder(view)
}