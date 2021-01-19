export class Estimate {
  private sentiment: string;
  private incomeClass: string;
  private gender: string;
  private count: number;

  constructor() {
  }

  public equals(that: Estimate): boolean {
    return this.sentiment === that.sentiment && this.incomeClass === that.incomeClass && this.gender === that.gender;
  }

  public setCount(count: number) {
    this.count = count;
  }

  public getCount(): number {
    return this.count;
  }
}
