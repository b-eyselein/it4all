import React from 'react';
import {MyJointClass} from './model/joint-class-diag-elements';
import {UmlAttribute, UmlClass, UmlClassType, UmlMethod, UmlVisibility} from '../../../graphql';
import {Field, FieldArray, Form, Formik} from 'formik';
import {useTranslation} from 'react-i18next';

interface IProps {
  editedClass: MyJointClass;
  cancelEdit: () => void;
}

const visibilities = ['+', '-', '#', '~'];
const umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

const umlClassTypes: UmlClassType[] = [UmlClassType.Abstract, UmlClassType.Interface, UmlClassType.Class];

function emptyAttribute(): UmlAttribute {
  return {memberType: umlTypes[umlTypes.length - 1], memberName: '', visibility: UmlVisibility.Public, isAbstract: false, isDerived: false, isStatic: false};
}

function emptyMethod(): UmlMethod {
  return {memberType: umlTypes[umlTypes.length - 1], memberName: '', visibility: UmlVisibility.Public, isAbstract: false, isStatic: false, parameters: ''};
}

export function UmlClassEdit({editedClass, cancelEdit}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const initialValues: UmlClass = {
    classType: UmlClassType.Class,
    name: editedClass.getClassName(),
    attributes: editedClass.getAttributes(),
    methods: editedClass.getMethods()
  };

  function onSubmit({classType, name, attributes, methods}: UmlClass): void {
    if (editedClass.getClassType() !== classType) {
      editedClass.setClassType(classType);
    }

    if (editedClass.getClassName() !== name) {
      editedClass.setClassName(name);
    }

    editedClass.setAttributes(attributes);
    editedClass.setMethods(methods);

    cancelEdit();
  }

  return (
    <Formik initialValues={initialValues} onSubmit={onSubmit}>
      {({values}) =>
        <Form>
          <div className="box">

            <div className="field has-addons">
              <div className="control">
                <div className="select">
                  <Field as="select" name="classType">
                    {umlClassTypes.map((classType) => <option key={classType}>{classType}</option>)}
                  </Field>
                </div>
              </div>
              <div className="control is-expanded">
                <Field name="name" className="input"/>
              </div>
            </div>

            <hr/>

            <FieldArray name="attributes">
              {(arrayHelpers) => <>

                {values.attributes.map((attr, index) =>
                  <div className="field has-addons" key={index}>
                    <div className="control">
                      <div className="select">
                        <Field as="select" name={`attributes.${index}.visibility`}>
                          {visibilities.map((visibility) => <option key={visibility}>{visibility}</option>)}
                        </Field>
                      </div>
                    </div>
                    <div className="control is-expanded">
                      <Field type="text" name={`attributes.${index}.memberName`} className="input" placeholder={t('name')}/>
                    </div>
                    <div className="control">
                      <button className="button is-static">:</button>
                    </div>
                    <div className="control">
                      <div className="select">
                        <Field as="select" name={`attributes.${index}.memberType`}>
                          {umlTypes.map((umlType) => <option key={umlType}>{umlType}</option>)}
                        </Field>
                      </div>
                    </div>
                    <div className="control">
                      <button onClick={() => arrayHelpers.remove(index)} className="button">-</button>
                    </div>
                  </div>
                )}

                <button type="button" onClick={() => arrayHelpers.push(emptyAttribute())} className="button">+</button>
              </>}
            </FieldArray>

            <hr/>

            <FieldArray name="methods">
              {(arrayHelpers) => <>

                {values.methods.map((method, index) => <div className="field has-addons" key={index}>
                  <div className="control">
                    <div className="select">
                      <Field as="select" name={`methods.${index}.visibility`}>
                        {visibilities.map((visibility) => <option key={visibility}>{visibility}</option>)}
                      </Field>
                    </div>
                  </div>
                  <div className="control">
                    <Field name={`methods.${index}.memberName`} className="input" placeholder={t('name')}/>
                  </div>
                  <div className="control">
                    <button className="button is-static">(</button>
                  </div>
                  <div className="control is-expanded">
                    <Field name={`methods.${index}.parameters`} className="input" placeholder={t('parameters')}/>
                  </div>
                  <div className="control">
                    <button className="button is-static">):</button>
                  </div>
                  <div className="control">
                    <div className="select">
                      <Field as="select" name={`methods.${index}.memberType`}>
                        {umlTypes.map((umlType) => <option key={umlType}>{umlType}</option>)}
                      </Field>
                    </div>
                  </div>
                  <div className="control">
                    <button className="button" onClick={() => arrayHelpers.remove(index)}>-</button>
                  </div>
                </div>)}

                <button type="button" onClick={() => arrayHelpers.push(emptyMethod())} className="button">+</button>
              </>}
            </FieldArray>

            <hr/>

            <div className="columns">
              <div className="column">
                <button type="button" className="button is-fullwidth" onClick={cancelEdit}>Verwerfen</button>
              </div>
              <div className="column">
                <button type="submit" className="button is-link is-fullwidth">Anwenden</button>
              </div>
            </div>
          </div>

        </Form>
      }
    </Formik>
  );
}
