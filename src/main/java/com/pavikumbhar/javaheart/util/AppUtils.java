
package com.pavikumbhar.javaheart.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author pavikumbhar
 *
 */
public class AppUtils {

	private AppUtils() {
	}

	/**
	 * Convert the ResultSet to a List of Maps, where each Map represents a row with
	 * columnNames and columValues
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			rows.add(row);
		}
		return rows;
	}

	/**
	 * Convert the ResultSet to a List of Maps, where each Map represents a row with
	 * columnNames and columValues
	 * 
	 * @param rs
	 * @return 
	 * @throws SQLException
	 */
	public List<Map<String, String>> convertResultSetToListOfMap(ResultSet rs) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> columns = new ArrayList<String>(rsmd.getColumnCount());
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columns.add(rsmd.getColumnName(i));
			}

			while (rs.next()) {
				Map<String, String> row = new HashMap<String, String>(columns.size());
				for (String col : columns) {
					row.put(col, rs.getString(col));
				}
				data.add(row);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 
	 * @param file
	 * @param t
	 * @return
	 */
	public <T> LinkedList<T> readCsv(File file, Class<T> t) {
		String[] columnNames = null;

		LinkedList<T> result = new LinkedList<>();
		try {
			try (BufferedReader reader = new BufferedReader(
					new FileReader(file))) {
				String line;
				boolean isColumnNames = true;
				while ((line = reader.readLine()) != null) {
					if (!StringUtils.isEmpty(line)) {
						String[] attributes = line.split(",");
						if (isColumnNames) {
							columnNames = attributes;
							isColumnNames = false;
						} else {
							T newInstance = t.newInstance();
							//Field[] fields = t.getDeclaredFields();
							List<Field>  fields= getInheritedPrivateFields(t);
							for (Field field : fields) {
								for (int i = 0; i < columnNames.length; i++) {
									if (field.getName().equalsIgnoreCase(columnNames[i])) {
										field.setAccessible(true);
										Object value = getValue(field,attributes[i]);
										field.set(newInstance, value);
									}
								}
							}
							result.add(newInstance);
						}
					}
				}
			} catch (InstantiationException ex) {

			} catch (IllegalAccessException ex) {

			}
		} catch (FileNotFoundException ex) {

		} catch (IOException ex) {

		}
		return result;
	}

	/**
	 * 
	 * @param field
	 * @param attribute
	 * @return
	 */
	public Object getValue(Field field, String attribute) {
		if (field.getType() == String.class) {
			return attribute;
		}
		if (field.getType() == Integer.class || field.getType() == int.class) {
			return Integer.parseInt(attribute);
		}
		if (field.getType() == Boolean.class|| field.getType() == boolean.class) {
			return Boolean.parseBoolean(attribute);
		}
		if (field.getType() == Double.class || field.getType() == double.class) {
			return Double.parseDouble(attribute);
		}
		if (field.getType() == Float.class || field.getType() == float.class) {
			return Float.parseFloat(attribute);
		}
		if (field.getType() == Long.class || field.getType() == long.class) {
			return Long.parseLong(attribute);
		}
		if (field.getType() == Date.class) {

		}
		return null;

	}

	/**
	 * 
	 * @param clazz
	 */
	public static <T> void converTO(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("[clazz] could not be null!");
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ((field.getType() == long.class) || (field.getType() == Long.class)) {
             
            } else if ((field.getType() == int.class) || (field.getType() == Integer.class)) {
             
            } else if ((field.getType() == char.class) || (field.getType() == Character.class)) {
             
            } else if ((field.getType() == short.class) || (field.getType() == Short.class)) {
              
            } else if ((field.getType() == double.class) || (field.getType() == Double.class)) {
              
            } else if ((field.getType() == float.class) || (field.getType() == Float.class)) {
              
            } else if ((field.getType() == boolean.class) || (field.getType() == Boolean.class)) {
              
            } else if (field.getType() == String.class) {
              
            } else if (field.getType() == Date.class) {
              
            }
        }

       
    }

	/**
	 * 
	 * @param type
	 * @return
	 */
	private static List<Field> getInheritedPrivateFields(Class<?> type) {
	    List<Field> result = new ArrayList<Field>();

	    Class<?> i = type;
	    while (i != null && i != Object.class) {
	        Collections.addAll(result, i.getDeclaredFields());
	        i = i.getSuperclass();
	    }

	    return result;
	}
	
	
	/**
	 * 
	 * @param classname
	 * @param searchPackages
	 * @return
	 */
	public static final Class<?> findClassByName(String classname,
			String[] searchPackages) {
		for (int i = 0; i < searchPackages.length; i++) {
			try {
				return Class.forName(searchPackages[i] + "." + classname);
			} catch (ClassNotFoundException e) {
				// not in this package, try another
			}
		}
		// nothing found: return null or throw ClassNotFoundException
		return null;
	}


}
