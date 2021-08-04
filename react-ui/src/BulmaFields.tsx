import React from 'react';
import {ErrorMessage, Field, FieldProps} from 'formik';

interface CustomInputFieldProps extends FieldProps {
  label: string;
  id: string;
  required?: boolean;
}

// eslint-disable-next-line
export function BulmaInputField({label, id, required, field, form, ...props}: CustomInputFieldProps): JSX.Element {
  return (
    <div className="field">
      <label htmlFor="id" className="label">{label}{required ? '*' : ''}:</label>
      <div className="control">
        <Field {...props} {...field} id={id} className="input" placeholder={label}/>
      </div>
      <ErrorMessage name={field.name}>{msg => <p className="help is-danger">{msg}</p>}</ErrorMessage>
    </div>
  );
}
