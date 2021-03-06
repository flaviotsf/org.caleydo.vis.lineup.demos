/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.wur;

import static university.wur.WorldUniversityYear.COL_academic;
import static university.wur.WorldUniversityYear.COL_citations;
import static university.wur.WorldUniversityYear.COL_employer;
import static university.wur.WorldUniversityYear.COL_faculty;
import static university.wur.WorldUniversityYear.COL_international;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;

import demo.RankTableDemo;
import demo.project.model.RankTableSpec;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldUniversityRanking extends AWorldUniversityRanking {

	public WorldUniversityRanking(RankTableSpec spec) {
		super(spec);
	}

	@Override
	public void apply(RankTableModel table) throws Exception {
		// qsrank schoolname qsstars overall academic employer faculty international internationalstudents citations
		// arts engineering life natural social

		Map<String, String> countries = WorldUniversityYear.readCountries();


		Map<String, WorldUniversityYear[]> data = WorldUniversityYear
				.readData(2013, 2012, 2011, 2010, 2009, 2008, 2007);
		countries.keySet().retainAll(data.keySet());

		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue(), countries.get(entry.getKey())));
		}
		table.addData(rows);
		data = null;

		if (tableSpec == null) {
			table.add(new RankRankColumnModel());
			table.add(createSchoolName());

			table.add(createCountries(countries.values()));

			int rankColWidth = 40;

			// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
			WorldUniversityYear.addYear(table, "2013", new YearGetter(0), false, false).orderByMe();

			// WorldUniversityYear.addSpecialYear(table, new YearGetter(0));

			addYear(rankColWidth, table, "2012", new YearGetter(1)).setCompressed(true);
			addYear(rankColWidth, table, "2011", new YearGetter(2)).setCompressed(true);
			addYear(rankColWidth, table, "2010", new YearGetter(3)).setCompressed(true);
			addYear(rankColWidth, table, "2009", new YearGetter(4)).setCollapsed(true);
			addYear(rankColWidth, table, "2008", new YearGetter(5)).setCollapsed(true);
			addYear(rankColWidth, table, "2007", new YearGetter(6)).setCollapsed(true);
		} else {
			parseSpec(table, countries.values());
		}
	}

	public static void dump() throws IOException {
		Map<String, String> countries = WorldUniversityYear.readCountries();

		int[] years = { 2012, 2011, 2010, 2009, 2008, 2007 };
		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(years);
		countries.keySet().retainAll(data.keySet());
		final char SEP = '\t';
		try (PrintWriter w = new PrintWriter(new File("wur_summary.csv"), "UTF-8")) {
			w.append("School name").append(SEP).append("Country");

			for (int year : years) {
				w.append(SEP).append(year + " ").append("Academic reputation");
				w.append(SEP).append(year + " ").append("Employer reputation");
				w.append(SEP).append(year + " ").append("Faculty/student ratio");
				w.append(SEP).append(year + " ").append("Citations per faculty");
				w.append(SEP).append(year + " ").append("International faculty ratio");
				w.append(SEP).append(year + " ").append("International student ratio");
			}
			w.println();

			for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
				w.append(entry.getKey()).append(SEP).append(Objects.toString(countries.get(entry.getKey()), ""));

				for (WorldUniversityYear y : entry.getValue()) {
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_academic)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_employer)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_faculty)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_citations)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_academic)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_international)));
				}
				w.println();
			}
		}
	}

	private static CharSequence toString(double f) {
		if (Double.isNaN(f))
			return "";
		return Double.toString(f);
	}

	public static void main(String[] args) {
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "world university ranking 2012,2011 and 2010",
				new WorldUniversityRanking(null));
	}
}
