import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'analyticsTableTimePerKbFormat',
  pure: true
})
export class AnalyticsTableTimePerKbFormatPipe implements PipeTransform {
  transform(time: number): string {
    return time + "<span class='response-time-per-kb-unity'> s / MB</span>";
  }
}
