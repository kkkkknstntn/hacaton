import React, { useState } from "react";
import ParamIcon from "@mui/icons-material/SettingsOutlined"

export interface AddInterestsProps {
    selectedInterests: number[]; 
    onSaveInterests: (interests: number[]) => void;
}



