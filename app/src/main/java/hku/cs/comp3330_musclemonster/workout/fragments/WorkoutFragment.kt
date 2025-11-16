package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel

class WorkoutFragment : Fragment() {

    companion object {
        fun newInstance() = WorkoutFragment()
    }

    private val viewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
        * TODO
        *  1. get current time -> populate into the textview
        * */

        return inflater.inflate(R.layout.fragment_workout, container, false)
    }
}