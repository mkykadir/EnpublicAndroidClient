package com.mkytr.enpublic.RestfulObjects;

import com.mkytr.enpublic.Database.LocationEntity;
import com.mkytr.enpublic.Database.TransitionEntity;

import java.util.List;

public class ActivityDetails {
    private List<TransitionEntity> transitions;
    private List<LocationEntity> locations;

    public ActivityDetails(List<TransitionEntity> transitions, List<LocationEntity> locations) {
        this.transitions = transitions;
        this.locations = locations;
    }

    public List<TransitionEntity> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionEntity> transitions) {
        this.transitions = transitions;
    }

    public List<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationEntity> locations) {
        this.locations = locations;
    }
}
