export class SelectValue<T = any> {

  public label: string|undefined;
  public value: T;
  public checked?: boolean;
  public disabled?: boolean;

  constructor(label: string|undefined, value: T, checked?: boolean, disabled?: boolean) {
    this.label = label;
    this.value = value;

    if (checked) {
      this.checked = checked;
    }
    if (disabled) {
      this.disabled = disabled;
    }
  }
}
