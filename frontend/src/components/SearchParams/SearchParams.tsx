import React from "react";
import { Radio, RadioGroup, FormControlLabel, FormLabel } from "@mui/material";
import styles from "./SearchParams.module.scss"

export interface SearchFieldProps {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

const SearchField: React.FC<SearchFieldProps> = ({ value, onChange }) => {
    return (
      <div className={styles.container}>
        <FormLabel
          component="legend"
          className={styles.label}
          sx={{
            color: "var(--error-contrast-text)", 
          }}
        >
          Пол
        </FormLabel>
        <RadioGroup row value={value} onChange={onChange}>
          <FormControlLabel
            value="MALE"
            control={<Radio color="secondary" className={styles.radio} />}
            label="Мужской"
          />
          <FormControlLabel
            value="FEMALE"
            control={<Radio color="secondary" className={styles.radio} />}
            label="Женский"
          />
          <FormControlLabel
            value="NONSTATE"
            control={<Radio color="secondary" className={styles.radio} />}
            label="Не важно"
          />
        </RadioGroup>
      </div>
    );
};

export default SearchField;