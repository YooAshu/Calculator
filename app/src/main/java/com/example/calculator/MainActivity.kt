package com.example.calculator

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import com.notkamui.keval.Keval


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var soundPool: SoundPool
    private var soundId1: Int = 0
    private var soundId2: Int = 0


    private var canAddOperator = false
    private var canAddDecimal = true
    private var canAddMinus = true
    private var showResult = false
    private var resetInput = false
    private var exprsn = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var inputText:TextView = findViewById<TextView>(R.id.inputText)
        var resultText:TextView = findViewById<TextView>(R.id.resultText)
        var minusButton:Button = findViewById<Button>(R.id.minus)

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,20,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,25,50,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)

//        inputText.setMovementMethod(new ScrollingMovementMethod())
//         inputText.movementMethod = ScrollingMovementMethod.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(1, android.media.AudioManager.STREAM_MUSIC, 0)
        }

        // Load the sound file
        soundId1 = soundPool.load(this, R.raw.buttonclick, 1)
        soundId2 = soundPool.load(this, R.raw.equalbutton, 1)






    }

    lateinit var inputText:TextView
    lateinit var resultText:TextView
    lateinit var minusButton:Button


    fun allClearButton(v: View?){
        soundPool.play(soundId1, 0.7f, 0.7f, 1, 0, 1.0f)

        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)
        //TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,35,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
       // TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,15,35,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        inputText.text = ""
        resultText.text = ""
        exprsn = ""
        canAddOperator = false
        canAddDecimal = true
        canAddMinus = true
        showResult = false
        resetInput = false

    }

    fun numButtonClick(view: View){

        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,20,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,25,50,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        if (view is Button){

            soundPool.play(soundId1, 0.7f, 0.7f, 1, 0, 1.0f)

            if(view.text=="." && canAddDecimal){
                inputText.append(view.text)
                exprsn += view.text
                canAddDecimal = false
                canAddMinus = false
//                evaluateAuto()
            }
            else if(view.text!="."){
                inputText.append(view.text)
                exprsn += view.text
                canAddOperator = true
                canAddMinus = true
                evaluateAuto()
            }

            if(resetInput){
                inputText.text = view.text
                exprsn = view.text.toString()
                resetInput = false
            }


        }


    }

    fun opButtonClick(view:View){
        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)
        minusButton = findViewById<Button>(R.id.minus)

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,20,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,25,50,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)



        if(view.id == R.id.minus && canAddMinus){
            soundPool.play(soundId1, 0.7f, 0.7f, 1, 0, 1.0f)
            inputText.append("-")
//            dammmn
            exprsn += "-"
            canAddOperator = false
            canAddDecimal = true
            showResult = true
            resetInput = false
            canAddMinus = false
            evaluateAuto()
        }
        else if (view is Button && canAddOperator){
            soundPool.play(soundId1, 0.7f, 0.7f, 1, 0, 1.0f)
            inputText.append(view.text)
            if (view.id == R.id.divide){
                exprsn += "/"
            }
            else if(view.id == R.id.multiply){
                exprsn += "*"
            }
            else if(view.id == R.id.minus){
                exprsn += "-"
            }
            else{
                exprsn += view.text
            }

            canAddOperator = false
            canAddDecimal = true
            showResult = true
            resetInput = false
            canAddMinus = false
            evaluateAuto()
        }

    }


    fun equalButton(v: View?){

        soundPool.play(soundId2, 0.7f, 0.7f, 1, 0, 1.0f)
        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)

        try{
            if (inputText.text.last().isDigit()){
                var res = Keval.eval(exprsn.toString()).toString()
                resultText.text = checkDoubleOrInt(res.toDouble()).toString()
            }
            else{
                var expressionLength = exprsn.length
                var res = Keval.eval(exprsn.subSequence(0,expressionLength-1).toString()).toString()
                resultText.text = checkDoubleOrInt(res.toDouble()).toString()
            }

        }
        catch (e:Exception){
            resultText.text = exprsn
        }

        showResult = false
        resetInput = true
        inputText.text = resultText.text
        exprsn = resultText.text.toString()
        resultText.text = ""

    }

    fun clearToLeft(v: View?){
        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,20,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,25,50,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        soundPool.play(soundId1, 0.7f, 0.7f, 1, 0, 1.0f)
        var expressionLength = exprsn.length

        if(expressionLength>0){
            var removedChar = exprsn.last()
            inputText.text = inputText.text.subSequence(0,expressionLength-1)
            exprsn = exprsn.subSequence(0,expressionLength-1).toString()
            if ((removedChar=='+') || (removedChar=='-') || (removedChar=='*') || (removedChar=='/')){
                canAddOperator = true
                canAddMinus = true
                canAddDecimal = false
                evaluateAuto()
            }
            else if(removedChar =='.'){
                canAddDecimal = true

            }
            else{
                canAddOperator = false
                canAddMinus = false
                evaluateAuto()
            }
            if(resetInput){
                resetInput=false
            }

        }

    }

    fun evaluateAuto(){
        inputText = findViewById<TextView>(R.id.inputText)
        resultText = findViewById<TextView>(R.id.resultText)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(inputText,15,20,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultText,25,50,1,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        if (showResult){
            inputText = findViewById<TextView>(R.id.inputText)
            resultText = findViewById<TextView>(R.id.resultText)


            try{
                if (inputText.text.last().isDigit()){
                    var res = Keval.eval(exprsn.toString()).toString()
                    resultText.text = checkDoubleOrInt(res.toDouble()).toString()
                }
                else{
                    var expressionLength = exprsn.length
                    var res = Keval.eval(exprsn.subSequence(0,expressionLength-1).toString()).toString()
                    resultText.text = checkDoubleOrInt(res.toDouble()).toString()
                }

            }
            catch (e:Exception){
                resultText.text = exprsn
            }


//            resultText.text = Keval.eval(inputText.text.toString()).toString()
        }
    }

    fun checkDoubleOrInt(result:Double): Any {
        var f = result.toInt()
        if(result-f.toDouble() == 0.0){
            return result.toInt()
        }
        else{
            return result
        }
    }





    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }





}


