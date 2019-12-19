import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  ViewChild,
  ViewContainerRef,
  ChangeDetectorRef,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { debounceTime } from 'rxjs/operators';
import debug from 'debug';

import {
  IPdfReportConfig,
  IPdfPage,
  IPdfCover,
  IPdfWidget,
  IPdfPageOrientation,
  IPdfPageType,
  IPdfWidgetType,
  IPdfWidgetCategory
} from 'analytics/src/models/pdf';

const log = debug('AnalyticsPdfEditionComponent');

@Component({
  selector: 'app-pdf-edition',
  templateUrl: './pdf-edition.component.html',
  styleUrls: ['./pdf-edition.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsPdfEditionComponent implements OnInit, OnChanges {
  formGroup: FormGroup;
  pdf: IPdfReportConfig;
  openedPanel = -1;
  pageTypeValues = Object.keys(IPdfPageType);
  orientationValues = Object.keys(IPdfPageOrientation);
  widgetTypeValues = this.createWidgetSelectList();
  widgetWidthValues = [25, 33, 50, 66, 75, 100];
  error = '';
  @Input() isPdfLoading = false;
  @Input() dates: { from: string; to: string };
  @Output() download = new EventEmitter<IPdfReportConfig>();
  @Output() setDates = new EventEmitter<{ from: string; to: string }>();
  @ViewChild('pdfContainer', { read: ViewContainerRef, static: false })
  pdfContainer: ViewContainerRef;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    try {
      this.pdf = JSON.parse(this.activatedRoute.snapshot.queryParams.pdf);
      log(JSON.stringify(this.pdf));
      // if a pdf config contains dates, we force the dates to the app state
      if (this.pdf.from && this.pdf.to) {
        this.setDates.emit({
          from: this.pdf.from,
          to: this.pdf.to
        });
      }
      this.formGroup = new FormGroup({
        from: new FormControl(this.pdf.from || this.dates.from, Validators.required),
        to: new FormControl(this.pdf.to || this.dates.to, Validators.required),
        pages: this.fb.array(this.pdf.pages.map((page: IPdfCover | IPdfPage) => this.getFormGroupPage(page)))
      });
      this.updateQueryParam();
    } catch (error) {
      this.error = 'PDF_CREATOR.ERROR_PARSING';
    }

    this.formGroup.valueChanges.pipe(debounceTime(300)).subscribe(value => {
      this.pdf = value;
      this.updateQueryParam();
      this.cdr.detectChanges();
      this.focusPage();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dates && !changes.dates.firstChange) {
      this.formGroup.get('from').setValue(changes.dates.currentValue.from);
      this.formGroup.get('to').setValue(changes.dates.currentValue.to);
    }
  }

  private createWidgetSelectList(): { name: string; list: string[] }[] {
    const widgetTypes = Object.keys(IPdfWidgetType);
    const widgetCategories = Object.keys(IPdfWidgetCategory);

    return widgetCategories.map(name => {
      return {
        name,
        list: widgetTypes.filter(widgetName => widgetName.indexOf(name) !== -1)
      };
    });
  }

  private updateQueryParam() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        pdf: JSON.stringify(this.formGroup.value)
      },
      queryParamsHandling: 'merge'
    });
  }

  public removePage(index: number) {
    (<FormArray>this.formGroup.get('pages')).removeAt(index);
    this.openedPanel = index > 0 ? index - 1 : 0;
  }

  public duplicatePage(index: number) {
    const newIndex = index + 1;
    const pageToCopy = (<FormArray>this.formGroup.get('pages')).at(index);
    console.log('pageToCopy', pageToCopy.value);

    (<FormArray>this.formGroup.get('pages')).insert(newIndex, this.getFormGroupPage(pageToCopy.value));
    this.openPanel(newIndex);
  }

  public pageDrop(e: CdkDragDrop<IPdfCover | IPdfPage>) {
    if (e.currentIndex === e.previousIndex) return;
    const pagesGroup = <FormArray>this.formGroup.get('pages');
    const pageToMove = pagesGroup.at(e.previousIndex);
    pagesGroup.removeAt(e.previousIndex);
    pagesGroup.insert(e.currentIndex, pageToMove);
  }

  public widgetDrop(pageIndex: number, e: CdkDragDrop<IPdfWidget>) {
    if (e.currentIndex === e.previousIndex) return;
    const widgetGroup = <FormArray>(<FormArray>this.formGroup.get('pages')).at(pageIndex).get('widgets');
    const pageToMove = widgetGroup.at(e.previousIndex);
    widgetGroup.removeAt(e.previousIndex);
    widgetGroup.insert(e.currentIndex, pageToMove);
  }

  public addNewPage(type: string) {
    const newIndex = (<FormArray>this.formGroup.get('pages')).length + 1;
    let newPage: FormGroup;
    if (type === IPdfPageType.COVER) {
      newPage = this.getFormGroupPage({
        type: IPdfPageType.COVER,
        orientation: IPdfPageOrientation.PORTRAIT,
        title: 'New Section'
      });
    } else {
      newPage = this.getFormGroupPage({
        type: IPdfPageType.PAGE,
        orientation: IPdfPageOrientation.PORTRAIT,
        widgets: []
      });
    }
    (<FormArray>this.formGroup.get('pages')).insert(newIndex, newPage);
  }

  public addNewWidget(pageIndex: number, widgetIndex: number) {
    const newIndex = widgetIndex + 1;
    (<FormArray>(<FormArray>this.formGroup.get('pages')).at(pageIndex).get('widgets')).insert(
      newIndex,
      this.getFormGroupWidget({
        type: IPdfWidgetType.BAR_TPPS_ROLES,
        info: false,
        width: 100,
        attributes: {}
      })
    );
  }

  public removeWidget(pageIndex: number, widgetIndex: number) {
    (<FormArray>(<FormArray>this.formGroup.get('pages')).at(pageIndex).get('widgets')).removeAt(widgetIndex);
  }

  public openPanel(index: number) {
    console.log('openPanel', index);
    this.openedPanel = index;
    this.cdr.detectChanges();
    this.scrollToPage(index);
  }

  public closePanel(index: number) {
    console.log('closePanel');
    if (index === this.openedPanel) {
      this.openedPanel = -1;
    }
  }

  private scrollToPage(index: number) {
    const pageToFocus = this.pdfContainer && this.pdfContainer.element.nativeElement.children[index];
    if (pageToFocus) {
      pageToFocus.scrollIntoView();
    }
  }

  private focusPage() {
    this.scrollToPage(this.openedPanel);
  }

  private getFormGroupPage(page: IPdfCover | IPdfPage): FormGroup {
    if (page.type === IPdfPageType.PAGE) {
      return this.fb.group({
        type: [page.type, Validators.required],
        orientation: [page.orientation, Validators.required],
        widgets: this.fb.array(
          page.widgets ? page.widgets.map((widget: IPdfWidget) => this.getFormGroupWidget(widget)) : []
        )
      });
    } else {
      return this.fb.group({
        type: [page.type, Validators.required],
        orientation: [page.orientation, Validators.required],
        title: [page.title, Validators.required]
      });
    }
  }

  private getFormGroupWidget(widget: IPdfWidget) {
    return this.fb.group({
      type: [widget.type, Validators.required],
      width: [widget.width, Validators.required],
      info: [widget.info, Validators.required],
      attributes: [widget.attributes]
    });
  }

  submit() {
    this.download.emit(this.formGroup.value);
  }
}
