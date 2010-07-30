/**
 * Copyright 2010 Samuel Kogler <samuel.kogler@gmail.com>
 *
 * This file is part of Scrumbeard.
 *
 * Scrumbeard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Scrumbeard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Scrumbeard.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.scrumbeard.shared;

/**
 *
 * @author daasdingo
 */
class TeamMember {
    private String name;
    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
